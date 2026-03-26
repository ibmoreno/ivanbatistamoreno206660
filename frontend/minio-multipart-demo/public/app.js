const state = {
  accessToken: "",
  uploadSession: null,
  aborted: false,
  autoRefreshEnabled: false,
  autoRefreshHandle: null,
  dashPlayerInstance: null
};

const elements = {
  apiBaseUrl: document.getElementById("apiBaseUrl"),
  username: document.getElementById("username"),
  password: document.getElementById("password"),
  albumId: document.getElementById("albumId"),
  chunkSizeMb: document.getElementById("chunkSizeMb"),
  videoFile: document.getElementById("videoFile"),
  loginButton: document.getElementById("loginButton"),
  clearTokenButton: document.getElementById("clearTokenButton"),
  uploadButton: document.getElementById("uploadButton"),
  abortButton: document.getElementById("abortButton"),
  loadVideosButton: document.getElementById("loadVideosButton"),
  autoRefreshButton: document.getElementById("autoRefreshButton"),
  stopPlayerButton: document.getElementById("stopPlayerButton"),
  copyResultButton: document.getElementById("copyResultButton"),
  clearLogsButton: document.getElementById("clearLogsButton"),
  authStatus: document.getElementById("authStatus"),
  libraryStatus: document.getElementById("libraryStatus"),
  playerStatus: document.getElementById("playerStatus"),
  videoCount: document.getElementById("videoCount"),
  summaryText: document.getElementById("summaryText"),
  progressBar: document.getElementById("progressBar"),
  partList: document.getElementById("partList"),
  resultBox: document.getElementById("resultBox"),
  logBox: document.getElementById("logBox"),
  videoGallery: document.getElementById("videoGallery"),
  dashPlayer: document.getElementById("dashPlayer")
};

elements.loginButton.addEventListener("click", handleLogin);
elements.clearTokenButton.addEventListener("click", clearToken);
elements.uploadButton.addEventListener("click", handleUpload);
elements.abortButton.addEventListener("click", handleAbort);
elements.loadVideosButton.addEventListener("click", loadAlbumVideos);
elements.autoRefreshButton.addEventListener("click", toggleAutoRefresh);
elements.stopPlayerButton.addEventListener("click", stopDashPlayback);
elements.copyResultButton.addEventListener("click", copyResult);
elements.clearLogsButton.addEventListener("click", () => {
  elements.logBox.innerHTML = "";
});

hydrateTokenFromStorage();
renderAuthState();

function getApiBaseUrl() {
  return elements.apiBaseUrl.value.trim().replace(/\/$/, "");
}

function getCurrentAlbumId() {
  return Number(elements.albumId.value);
}

function getHeaders(authenticated = true) {
  const headers = { "Content-Type": "application/json" };
  if (authenticated && state.accessToken) {
    headers.Authorization = `Bearer ${state.accessToken}`;
  }
  return headers;
}

function log(message, type = "info") {
  const line = document.createElement("div");
  const time = new Date().toLocaleTimeString("pt-BR");
  line.className = `log-line ${type}`;
  line.textContent = `[${time}] ${message}`;
  elements.logBox.prepend(line);
}

function setResult(value) {
  elements.resultBox.textContent =
    typeof value === "string" ? value : JSON.stringify(value, null, 2);
}

function setProgress(percent, summary) {
  elements.progressBar.style.width = `${percent}%`;
  elements.summaryText.textContent = summary;
}

function renderAuthState() {
  const authenticated = Boolean(state.accessToken);
  elements.authStatus.textContent = authenticated
    ? "Token JWT carregado e pronto para uso."
    : "Ainda não autenticado.";
  elements.authStatus.className = `status ${authenticated ? "success" : "neutral"}`;
}

function hydrateTokenFromStorage() {
  const token = window.localStorage.getItem("demoAccessToken");
  if (token) {
    state.accessToken = token;
  }
}

function persistToken(token) {
  state.accessToken = token;
  window.localStorage.setItem("demoAccessToken", token);
  renderAuthState();
}

function clearToken() {
  state.accessToken = "";
  window.localStorage.removeItem("demoAccessToken");
  renderAuthState();
  log("Token removido da sessão.", "info");
}

async function handleLogin() {
  try {
    const apiBaseUrl = getApiBaseUrl();
    const payload = {
      username: elements.username.value.trim(),
      password: elements.password.value
    };

    log(`Autenticando em ${apiBaseUrl}/api/v1/auth/login`);
    const response = await fetch(`${apiBaseUrl}/api/v1/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const data = await parseJsonResponse(response);
    if (!response.ok) {
      throw new Error(data.message || "Falha no login.");
    }

    persistToken(data.accessToken);
    setResult(data);
    log("Login concluído com sucesso.", "success");
  } catch (error) {
    elements.authStatus.textContent = `Erro no login: ${error.message}`;
    elements.authStatus.className = "status error";
    log(`Erro no login: ${error.message}`, "error");
  }
}

async function handleUpload() {
  const file = elements.videoFile.files[0];
  if (!file) {
    log("Selecione um arquivo de vídeo antes de iniciar.", "error");
    return;
  }

  if (!state.accessToken) {
    log("Faça login primeiro para obter o JWT.", "error");
    return;
  }

  const chunkSizeMb = Number(elements.chunkSizeMb.value);
  if (!Number.isFinite(chunkSizeMb) || chunkSizeMb < 5) {
    log("O tamanho do chunk deve ser de pelo menos 5 MB.", "error");
    return;
  }

  const albumId = getCurrentAlbumId();
  if (!Number.isFinite(albumId) || albumId < 1) {
    log("Informe um ID de álbum válido.", "error");
    return;
  }

  state.aborted = false;
  elements.uploadButton.disabled = true;
  elements.abortButton.disabled = false;
  setResult("Upload em andamento...");

  try {
    const partSizeBytes = chunkSizeMb * 1024 * 1024;
    const totalParts = Math.ceil(file.size / partSizeBytes);
    initializePartList(totalParts);
    setProgress(0, `Preparando ${totalParts} partes para ${file.name}`);

    const sessionResponse = await startMultipartUpload({
      albumId,
      file,
      totalParts
    });

    const session = {
      ...sessionResponse,
      albumId
    };

    state.uploadSession = session;
    log(`Upload iniciado com uploadId ${session.uploadId}`, "success");

    const uploadedParts = [];

    for (const part of session.parts) {
      if (state.aborted) {
        throw new Error("Upload abortado pelo usuário.");
      }

      updatePartState(part.partNumber, "uploading", "Enviando para o MinIO");
      const chunk = getChunk(file, part.partNumber, partSizeBytes);
      const etag = await uploadPart(part.url, chunk, part.partNumber);
      uploadedParts.push({ partNumber: part.partNumber, eTag: etag });

      const percent = Math.round((uploadedParts.length / session.parts.length) * 100);
      updatePartState(part.partNumber, "done", `ETag recebido: ${etag}`);
      setProgress(percent, `${uploadedParts.length}/${session.parts.length} partes enviadas`);
    }

    const completed = await completeMultipartUpload({
      albumId,
      objectKey: session.objectKey,
      uploadId: session.uploadId,
      parts: uploadedParts
    });

    setResult(completed);
    setProgress(100, "Upload concluído com sucesso.");
    log(`Upload finalizado. URL assinada gerada para ${completed.objectKey}`, "success");
    await loadAlbumVideos();
  } catch (error) {
    if (!state.aborted) {
      log(`Falha no upload: ${error.message}`, "error");
      setResult({ error: error.message });
      setProgress(0, "Upload interrompido.");
    }
  } finally {
    elements.uploadButton.disabled = false;
    elements.abortButton.disabled = true;
    state.uploadSession = null;
  }
}

async function startMultipartUpload({ albumId, file, totalParts }) {
  const response = await fetch(`${getApiBaseUrl()}/api/v1/album/${albumId}/video/multipart`, {
    method: "POST",
    headers: getHeaders(true),
    body: JSON.stringify({
      fileName: file.name,
      contentType: file.type || "video/mp4",
      totalParts
    })
  });

  const data = await parseJsonResponse(response);
  if (!response.ok) {
    throw new Error(data.message || "Não foi possível iniciar o multipart upload.");
  }

  return data;
}

function getChunk(file, partNumber, partSizeBytes) {
  const start = (partNumber - 1) * partSizeBytes;
  const end = Math.min(file.size, start + partSizeBytes);
  return file.slice(start, end);
}

async function uploadPart(url, chunk, partNumber) {
  log(`Enviando parte ${partNumber} para URL assinada do MinIO`);
  const response = await fetch(url, {
    method: "PUT",
    body: chunk
  });

  if (!response.ok) {
    throw new Error(`Parte ${partNumber} falhou com status ${response.status}.`);
  }

  const etag = response.headers.get("ETag");
  if (!etag) {
    throw new Error(`Parte ${partNumber} foi enviada, mas o header ETag não ficou exposto via CORS.`);
  }

  return etag;
}

async function completeMultipartUpload({ albumId, objectKey, uploadId, parts }) {
  log("Solicitando a conclusão do multipart upload ao backend.");
  const response = await fetch(`${getApiBaseUrl()}/api/v1/album/${albumId}/video/multipart/complete`, {
    method: "POST",
    headers: getHeaders(true),
    body: JSON.stringify({
      objectKey,
      uploadId,
      parts
    })
  });

  const data = await parseJsonResponse(response);
  if (!response.ok) {
    throw new Error(data.message || "Não foi possível concluir o multipart upload.");
  }

  return data;
}

async function handleAbort() {
  if (!state.uploadSession) {
    return;
  }

  state.aborted = true;

  try {
    const { albumId, objectKey, uploadId } = state.uploadSession;
    log("Abortando multipart upload no backend.");
    const response = await fetch(
      `${getApiBaseUrl()}/api/v1/album/${albumId}/video/multipart?objectKey=${encodeURIComponent(objectKey)}&uploadId=${encodeURIComponent(uploadId)}`,
      {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${state.accessToken}`
        }
      }
    );

    const data = await parseJsonResponse(response);
    if (!response.ok) {
      throw new Error(data.message || "Falha ao abortar o upload.");
    }

    setResult(data);
    setProgress(0, "Upload abortado.");
    log("Upload abortado com sucesso.", "warning");
    await loadAlbumVideos();
  } catch (error) {
    log(`Erro ao abortar upload: ${error.message}`, "error");
  } finally {
    elements.abortButton.disabled = true;
  }
}

async function loadAlbumVideos() {
  const albumId = getCurrentAlbumId();
  if (!Number.isFinite(albumId) || albumId < 1) {
    log("Informe um ID de álbum válido para listar os vídeos.", "error");
    return;
  }

  if (!state.accessToken) {
    log("Faça login antes de consultar a biblioteca.", "error");
    return;
  }

  try {
    elements.libraryStatus.textContent = "Carregando vídeos do álbum...";
    elements.libraryStatus.className = "status neutral";

    const response = await fetch(`${getApiBaseUrl()}/api/v1/album/${albumId}/video`, {
      method: "GET",
      headers: {
        Authorization: `Bearer ${state.accessToken}`
      }
    });

    const data = await parseJsonResponse(response);
    if (!response.ok) {
      throw new Error(data.message || "Não foi possível listar os vídeos.");
    }

    renderVideoGallery(Array.isArray(data) ? data : []);
    setResult(data);
    elements.libraryStatus.textContent = "Biblioteca atualizada com sucesso.";
    elements.libraryStatus.className = "status success";
    log(`Biblioteca do álbum ${albumId} carregada com ${data.length} item(ns).`, "success");
  } catch (error) {
    renderVideoGallery([]);
    elements.libraryStatus.textContent = `Erro ao carregar vídeos: ${error.message}`;
    elements.libraryStatus.className = "status error";
    log(`Erro ao carregar vídeos: ${error.message}`, "error");
  }
}

function renderVideoGallery(videos) {
  elements.videoCount.textContent = `${videos.length} item(ns) carregados.`;

  if (!videos.length) {
    elements.videoGallery.className = "video-gallery empty-state";
    elements.videoGallery.textContent = "Nenhum vídeo encontrado para este álbum.";
    return;
  }

  elements.videoGallery.className = "video-gallery";
  elements.videoGallery.innerHTML = "";

  for (const video of videos) {
    const card = document.createElement("article");
    card.className = "video-card";
    card.innerHTML = createVideoCardMarkup(video);

    const playDashButton = card.querySelector("[data-action='play-dash']");
    const usePreviewButton = card.querySelector("[data-action='play-preview']");

    if (playDashButton) {
      playDashButton.addEventListener("click", () => startDashPlayback(video));
    }

    if (usePreviewButton) {
      usePreviewButton.addEventListener("click", () => playPreviewInMainPlayer(video));
    }

    elements.videoGallery.appendChild(card);
  }
}

function createVideoCardMarkup(video) {
  const uploadStatus = normalizeStatus(video.uploadStatus);
  const processingStatus = normalizeStatus(video.processingStatus);
  const duration = video.durationSeconds ? `${video.durationSeconds}s` : "Aguardando";
  const createdAt = video.createdAt ? formatDateTime(video.createdAt) : "-";
  const processedAt = video.processedAt ? formatDateTime(video.processedAt) : "-";

  return `
    <div class="video-media">
      ${createMediaMarkup(video)}
    </div>
    <div class="video-card-body">
      <h3 class="video-title">${escapeHtml(video.originalFileName || video.objectKey || "Vídeo sem nome")}</h3>
      <div class="video-meta">
        <span class="status-chip ${uploadStatus.cssClass}">Upload: ${uploadStatus.label}</span>
        <span class="status-chip ${processingStatus.cssClass}">Pipeline: ${processingStatus.label}</span>
      </div>
      <div class="video-grid">
        <div class="video-metric"><strong>Duração</strong>${duration}</div>
        <div class="video-metric"><strong>Partes</strong>${video.totalParts || "-"}</div>
        <div class="video-metric"><strong>Criado em</strong>${createdAt}</div>
        <div class="video-metric"><strong>Processado em</strong>${processedAt}</div>
      </div>
      <div class="video-actions">
        ${video.previewUrl ? `<button class="button ghost" type="button" data-action="play-preview">Abrir preview</button>` : ""}
        ${video.dashManifestUrl ? `<button class="button primary" type="button" data-action="play-dash">Abrir DASH</button>` : ""}
        ${video.rendition360Url ? `<a class="button ghost link" target="_blank" rel="noreferrer" href="${video.rendition360Url}">360p</a>` : ""}
        ${video.rendition720Url ? `<a class="button ghost link" target="_blank" rel="noreferrer" href="${video.rendition720Url}">720p</a>` : ""}
      </div>
    </div>
  `;
}

function createMediaMarkup(video) {
  if (video.thumbnailUrl) {
    return `<img src="${video.thumbnailUrl}" alt="Thumbnail do vídeo ${escapeHtml(video.originalFileName || "sem nome")}">`;
  }

  if (video.previewUrl) {
    return `<video src="${video.previewUrl}" muted preload="metadata"></video>`;
  }

  return `<div class="video-placeholder">Processamento ainda sem thumbnail disponível.</div>`;
}

function startDashPlayback(video) {
  if (!video.dashManifestUrl) {
    log("Este vídeo ainda não possui manifesto DASH disponível.", "warning");
    return;
  }

  stopDashPlayback();

  state.dashPlayerInstance = dashjs.MediaPlayer().create();
  state.dashPlayerInstance.initialize(elements.dashPlayer, video.dashManifestUrl, true);
  elements.playerStatus.textContent = `Player DASH ativo para ${video.originalFileName || video.objectKey}`;
  log(`Player DASH iniciado para ${video.dashManifestUrl}`, "success");
}

function playPreviewInMainPlayer(video) {
  if (!video.previewUrl) {
    log("Este vídeo ainda não possui preview curto disponível.", "warning");
    return;
  }

  stopDashPlayback();
  elements.dashPlayer.src = video.previewUrl;
  elements.dashPlayer.play().catch(() => {});
  elements.playerStatus.textContent = `Reproduzindo preview curto de ${video.originalFileName || video.objectKey}`;
  log(`Preview curto aberto para ${video.previewUrl}`, "success");
}

function stopDashPlayback() {
  if (state.dashPlayerInstance) {
    state.dashPlayerInstance.reset();
    state.dashPlayerInstance = null;
  }
  elements.dashPlayer.pause();
  elements.dashPlayer.removeAttribute("src");
  elements.dashPlayer.load();
  elements.playerStatus.textContent = "Selecione um vídeo com manifesto DASH para iniciar.";
}

function toggleAutoRefresh() {
  state.autoRefreshEnabled = !state.autoRefreshEnabled;
  elements.autoRefreshButton.textContent = state.autoRefreshEnabled
    ? "Desativar auto refresh"
    : "Ativar auto refresh";

  if (state.autoRefreshEnabled) {
    state.autoRefreshHandle = window.setInterval(loadAlbumVideos, 10000);
    log("Auto refresh ativado a cada 10 segundos.", "info");
    loadAlbumVideos();
  } else {
    window.clearInterval(state.autoRefreshHandle);
    state.autoRefreshHandle = null;
    log("Auto refresh desativado.", "info");
  }
}

function initializePartList(totalParts) {
  elements.partList.innerHTML = "";
  for (let partNumber = 1; partNumber <= totalParts; partNumber += 1) {
    const row = document.createElement("div");
    row.className = "part-row";
    row.id = `part-${partNumber}`;
    row.innerHTML = `
      <span class="part-chip">Parte ${partNumber}</span>
      <span class="part-state pending">Aguardando envio</span>
      <span class="part-meta">-</span>
    `;
    elements.partList.appendChild(row);
  }
}

function updatePartState(partNumber, status, meta) {
  const row = document.getElementById(`part-${partNumber}`);
  if (!row) {
    return;
  }

  const stateNode = row.querySelector(".part-state");
  const metaNode = row.querySelector(".part-meta");
  stateNode.className = `part-state ${status}`;
  stateNode.textContent = translatePartStatus(status);
  metaNode.textContent = meta;
}

function translatePartStatus(status) {
  switch (status) {
    case "uploading":
      return "Enviando";
    case "done":
      return "Concluída";
    case "error":
      return "Erro";
    default:
      return "Pendente";
  }
}

function normalizeStatus(status) {
  const normalized = (status || "UNKNOWN").toLowerCase().replace(/_/g, "-");
  return {
    cssClass: normalized,
    label: status || "UNKNOWN"
  };
}

function formatDateTime(value) {
  try {
    return new Date(value).toLocaleString("pt-BR");
  } catch {
    return value;
  }
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#039;");
}

async function parseJsonResponse(response) {
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  const text = await response.text();
  return text ? { message: text } : {};
}

async function copyResult() {
  try {
    await navigator.clipboard.writeText(elements.resultBox.textContent);
    log("JSON copiado para a área de transferência.", "success");
  } catch (error) {
    log(`Não foi possível copiar o resultado: ${error.message}`, "error");
  }
}
