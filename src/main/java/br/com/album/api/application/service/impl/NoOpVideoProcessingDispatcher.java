package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.VideoProcessingDispatcher;
import br.com.album.api.infra.database.jpa.VideoUploadEntity;
import org.springframework.stereotype.Service;

@Service
public class NoOpVideoProcessingDispatcher implements VideoProcessingDispatcher {

    @Override
    public VideoUploadEntity prepare(VideoUploadEntity videoUploadEntity) {
        return videoUploadEntity;
    }
}
