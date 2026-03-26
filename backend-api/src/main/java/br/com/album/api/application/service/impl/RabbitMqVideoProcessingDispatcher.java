package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.VideoProcessingDispatcher;
import br.com.album.api.application.service.dto.VideoProcessingMessage;
import br.com.album.api.infra.database.jpa.VideoProcessingStatus;
import br.com.album.api.infra.database.jpa.VideoUploadEntity;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "video-processing.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqVideoProcessingDispatcher implements VideoProcessingDispatcher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${video-processing.exchange}")
    private String exchange;

    @Value("${video-processing.routing-key}")
    private String routingKey;

    @Override
    public VideoUploadEntity prepare(VideoUploadEntity videoUploadEntity) {
        String processingReference = videoUploadEntity.getProcessingReference();
        if (processingReference == null || processingReference.isBlank()) {
            processingReference = UUID.randomUUID().toString();
            videoUploadEntity.setProcessingReference(processingReference);
        }

        videoUploadEntity.setProcessingStatus(VideoProcessingStatus.QUEUED);

        rabbitTemplate.convertAndSend(exchange, routingKey, VideoProcessingMessage.builder()
                .videoUploadId(videoUploadEntity.getId())
                .albumId(videoUploadEntity.getAlbum().getId())
                .bucket(videoUploadEntity.getBucket())
                .objectKey(videoUploadEntity.getObjectKey())
                .uploadId(videoUploadEntity.getUploadId())
                .originalFileName(videoUploadEntity.getOriginalFileName())
                .contentType(videoUploadEntity.getContentType())
                .totalParts(videoUploadEntity.getTotalParts())
                .processingReference(processingReference)
                .completedAt(videoUploadEntity.getCompletedAt())
                .build());

        return videoUploadEntity;
    }
}
