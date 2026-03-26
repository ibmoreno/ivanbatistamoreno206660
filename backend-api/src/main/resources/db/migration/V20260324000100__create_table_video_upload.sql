CREATE TABLE video_upload
(
    id                   BIGSERIAL PRIMARY KEY,
    id_album             BIGINT       NOT NULL,
    bucket               VARCHAR(100) NOT NULL,
    object_key           VARCHAR(255) NOT NULL UNIQUE,
    upload_id            VARCHAR(255) NOT NULL,
    original_file_name   VARCHAR(255) NOT NULL,
    content_type         VARCHAR(100) NOT NULL,
    total_parts          INTEGER      NOT NULL,
    upload_status        VARCHAR(30)  NOT NULL,
    processing_status    VARCHAR(30)  NOT NULL,
    processing_reference VARCHAR(255) NULL,
    failure_reason       VARCHAR(500) NULL,
    completed_at         TIMESTAMP    NULL,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_video_upload_album
        FOREIGN KEY (id_album)
            REFERENCES album(id)
            ON DELETE CASCADE
);

CREATE INDEX idx_video_upload_album ON video_upload (id_album);
CREATE INDEX idx_video_upload_status ON video_upload (upload_status, processing_status);
