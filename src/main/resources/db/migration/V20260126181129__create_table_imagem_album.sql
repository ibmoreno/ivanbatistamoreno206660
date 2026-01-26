-- create table para armazenar as capas do algum
CREATE TABLE capa_album
(
    id       BIGSERIAL PRIMARY KEY,
    id_album BIGINT NOT NULL,
    bucket   varchar(50) NULL,
    hash     varchar(50) NULL
);