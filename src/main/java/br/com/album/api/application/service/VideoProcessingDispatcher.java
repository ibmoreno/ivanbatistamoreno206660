package br.com.album.api.application.service;

import br.com.album.api.infra.database.jpa.VideoUploadEntity;

public interface VideoProcessingDispatcher {
    VideoUploadEntity prepare(VideoUploadEntity videoUploadEntity);
}
