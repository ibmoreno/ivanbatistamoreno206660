const state = {
  accessToken: "",
  uploadSession: null,
  aborted: false
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
  copyResultButton: document.getElementById("copyResultButton"),
  clearLogsButton: document.getElementById("clearLogsButton"),
  authStatus: document.getElementById("authStatus"),
  summaryText: document.getElementById("summaryText"),
  progressBar: document.getElementById("progressBar"),
  partList: document.getElementById("partList"),
  resultBox: document.getElementById("resultBox"),
  logBox: document.getElementById("logBox")
};

elements.loginButton.addEventListener("click", handleLogin);
elements.clearTokenButton.addEventListener("click", clearToken);
elements.uploadButton.addEventListener("click", handleUpload);
elements.abortButton.addEventListener("click", handleAbort);
elements.copyResultButton.addEventListener("click", copyResult);
elements.clearLogsButton.addEventListener("click", () => {
  elements.logBox.innerHTML = "";
});

hydrateTokenFromStorage();
renderAuthState();

function getApiBaseUrl() {
  return elements.apiBaseUrl.value.trim().replace(/\/$/, "");
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

  const albumId = Number(elements.albumId.value);
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
  } catch (error) {
    log(`Erro ao abortar upload: ${error.message}`, "error");
  } finally {
    elements.abortButton.disabled = true;
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
  stateNode.textContent = translateStatus(status);
  metaNode.textContent = meta;
}

function translateStatus(status) {
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
