package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.VideoProcessingDispatcher;
import br.com.album.api.infra.database.jpa.VideoProcessingStatus;
import br.com.album.api.infra.database.jpa.VideoUploadEntity;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(VideoProcessingDispatcher.class)
public class NoOpVideoProcessingDispatcher implements VideoProcessingDispatcher {

    @Override
    public VideoUploadEntity prepare(VideoUploadEntity videoUploadEntity) {
        if (videoUploadEntity.getProcessingReference() == null || videoUploadEntity.getProcessingReference().isBlank()) {
            videoUploadEntity.setProcessingReference(UUID.randomUUID().toString());
        }
        videoUploadEntity.setProcessingStatus(VideoProcessingStatus.PENDING);
        return videoUploadEntity;
    }
}
