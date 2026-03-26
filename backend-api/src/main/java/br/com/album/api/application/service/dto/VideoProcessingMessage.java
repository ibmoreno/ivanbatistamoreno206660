package br.com.album.api.application.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VideoProcessingMessage(
        Long videoUploadId,
        Long albumId,
        String bucket,
        String objectKey,
        String uploadId,
        String originalFileName,
        String contentType,
        Integer totalParts,
        String processingReference,
        LocalDateTime completedAt
) {
}
