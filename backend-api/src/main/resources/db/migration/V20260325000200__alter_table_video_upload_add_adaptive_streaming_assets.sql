ALTER TABLE video_upload
    ADD COLUMN rendition_360_object_key VARCHAR(255) NULL,
    ADD COLUMN rendition_720_object_key VARCHAR(255) NULL,
    ADD COLUMN dash_manifest_object_key VARCHAR(255) NULL;
