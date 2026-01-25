-- criação da tabela artista
CREATE TABLE artista
(
    id   BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL
);

-- criação da tabela album
CREATE TABLE album
(
    id     BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(300) NOT NULL
);

-- criação da tabela artista_album
CREATE TABLE artista_album
(
    id_artista BIGINT NOT NULL,
    id_album   BIGINT NOT NULL,

    CONSTRAINT pk_artista_album
        PRIMARY KEY (id_artista, id_album),

    CONSTRAINT fk_artista_album_artista
        FOREIGN KEY (id_artista)
            REFERENCES artista(id)
            ON DELETE CASCADE,

    CONSTRAINT fk_artista_album_album
        FOREIGN KEY (id_album)
            REFERENCES album(id)
            ON DELETE CASCADE
);