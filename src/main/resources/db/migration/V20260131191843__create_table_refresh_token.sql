CREATE TABLE refresh_token (
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(20)  NOT NULL,
    token       VARCHAR(36)  NOT NULL,
    expira_em   TIMESTAMP    NOT NULL
);

CREATE INDEX idx_refresh_token_username ON refresh_token(token);