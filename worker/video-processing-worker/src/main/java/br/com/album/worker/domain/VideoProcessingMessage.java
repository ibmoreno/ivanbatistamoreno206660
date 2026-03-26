package br.com.album.worker.domain;

import java.time.LocalDateTime;

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
