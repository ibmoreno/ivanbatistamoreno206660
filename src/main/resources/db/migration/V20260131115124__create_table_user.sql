CREATE TABLE usuario
(
    id           BIGSERIAL PRIMARY KEY,
    nome         VARCHAR(200) NOT NULL,
    username     VARCHAR(20)  NOT NULL,
    password     VARCHAR(100) NOT NULL,
    perfil       VARCHAR(20)  NOT NULL,
    ativo        BOOLEAN      NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_username ON usuario(username);

INSERT INTO usuario (nome, username, password, perfil)
VALUES('Usuário para Acesso e Testes', 'user', '$2a$10$IOSiPoS5MatAHLNnoXu5BOvurX1LAnUChjJZ7BwTjdNK.A77Ji2dC', 'USER_ROLE');

commit;