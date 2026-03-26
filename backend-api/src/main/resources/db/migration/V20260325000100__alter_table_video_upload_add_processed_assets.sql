ALTER TABLE video_upload
    ADD COLUMN thumbnail_object_key VARCHAR(255) NULL,
    ADD COLUMN preview_object_key VARCHAR(255) NULL,
    ADD COLUMN duration_seconds BIGINT NULL,
    ADD COLUMN processed_at TIMESTAMP NULL;
