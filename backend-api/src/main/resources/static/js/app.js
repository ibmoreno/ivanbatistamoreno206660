function connect() {
    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/album-criado', function (message) {
            console.log("Payload:", message.body);
            const data = JSON.parse(message.body);
            renderAlbum(data)
        });
    });
}

function renderAlbum(album) {
    const container = document.getElementById("notifications");

    const card = document.createElement("div");
    card.className = "card";

    const titulo = document.createElement("h2");
    titulo.textContent = album.titulo;

    const artistasDiv = document.createElement("div");
    artistasDiv.className = "artistas";

    album.artistas.forEach(a => {
        const badge = document.createElement("span");
        badge.className = "badge";
        badge.textContent = a;
        artistasDiv.appendChild(badge);
    });

    const id = document.createElement("div");
    id.className = "id";
    id.textContent = `ID do álbum: ${album.id}`;

    card.appendChild(titulo);
    card.appendChild(artistasDiv);
    card.appendChild(id);

    // adiciona no topo (mais recente primeiro)
    container.prepend(card);
}

// conecta após carregar DOM
document.addEventListener("DOMContentLoaded", connect);
