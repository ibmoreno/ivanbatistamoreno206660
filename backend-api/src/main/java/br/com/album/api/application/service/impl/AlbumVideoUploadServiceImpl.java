package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.AlbumVideoUploadService;
import br.com.album.api.application.service.VideoProcessingDispatcher;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.bucket.MinioRepository;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.VideoProcessingStatus;
import br.com.album.api.infra.database.jpa.VideoUploadEntity;
import br.com.album.api.infra.database.jpa.VideoUploadStatus;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.infra.database.repository.VideoUploadRepository;
import br.com.album.api.presentation.controller.dto.AbortVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.CompleteVideoMultipartUploadRequest;
import br.com.album.api.presentation.controller.dto.CompleteVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.CreateVideoMultipartUploadRequest;
import br.com.album.api.presentation.controller.dto.CreateVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.PresignedVideoUploadPartResponse;
import br.com.album.api.presentation.controller.dto.VideoUploadResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumVideoUploadServiceImpl implements AlbumVideoUploadService {

    private final AlbumRepository albumRepository;
    private final VideoUploadRepository videoUploadRepository;
    private final MinioRepository minioRepository;
    private final VideoProcessingDispatcher videoProcessingDispatcher;

    @Value("${minio.bucketName}")
    private String bucket;

    @Value("${minio.multipart.partSizeMb:10}")
    private Integer partSizeMb;

    @Override
    public CreateVideoMultipartUploadResponse createMultipartUpload(Long idAlbum, CreateVideoMultipartUploadRequest request) {
        AlbumEntity albumEntity = getAlbum(idAlbum);
        String objectKey = buildObjectKey(idAlbum, request.getFileName());
        String uploadId = minioRepository.createMultipartUpload(bucket, objectKey);
        VideoUploadEntity videoUploadEntity = videoUploadRepository.save(VideoUploadEntity.builder()
                .album(albumEntity)
                .bucket(bucket)
                .objectKey(objectKey)
                .uploadId(uploadId)
                .originalFileName(request.getFileName())
                .contentType(request.getContentType())
                .totalParts(request.getTotalParts())
                .uploadStatus(VideoUploadStatus.IN_PROGRESS)
                .processingStatus(VideoProcessingStatus.NOT_SCHEDULED)
                .build());

        List<PresignedVideoUploadPartResponse> parts = buildPresignedParts(request.getTotalParts(), objectKey, uploadId);
        return CreateVideoMultipartUploadResponse.builder()
                .id(videoUploadEntity.getId())
                .idAlbum(idAlbum)
                .objectKey(objectKey)
                .uploadId(uploadId)
                .partSizeMb(partSizeMb)
                .contentType(request.getContentType())
                .uploadStatus(videoUploadEntity.getUploadStatus())
                .processingStatus(videoUploadEntity.getProcessingStatus())
                .createdAt(videoUploadEntity.getCreatedAt())
                .parts(parts)
                .build();
    }

    @Override
    public CompleteVideoMultipartUploadResponse completeMultipartUpload(Long idAlbum, CompleteVideoMultipartUploadRequest request) {
        getAlbum(idAlbum);
        VideoUploadEntity videoUploadEntity = getVideoUpload(idAlbum, request.getObjectKey(), request.getUploadId());
        try {
            minioRepository.completeMultipartUpload(
                    bucket,
                    request.getObjectKey(),
                    request.getUploadId(),
                    request.getParts().stream()
                            .map(part -> new MinioRepository.MultipartUploadPart(part.getPartNumber(), part.getETag()))
                            .toList()
            );

            videoUploadEntity.setUploadStatus(VideoUploadStatus.COMPLETED);
            videoUploadEntity.setProcessingStatus(VideoProcessingStatus.PENDING);
            videoUploadEntity.setFailureReason(null);
            videoUploadEntity.setCompletedAt(LocalDateTime.now());
            videoUploadEntity = videoUploadRepository.save(videoProcessingDispatcher.prepare(videoUploadEntity));

            return CompleteVideoMultipartUploadResponse.builder()
                    .id(videoUploadEntity.getId())
                    .idAlbum(idAlbum)
                    .objectKey(request.getObjectKey())
                    .url(minioRepository.getUrlFile(request.getObjectKey(), bucket))
                    .uploadStatus(videoUploadEntity.getUploadStatus())
                    .processingStatus(videoUploadEntity.getProcessingStatus())
                    .completedAt(videoUploadEntity.getCompletedAt())
                    .build();
        } catch (RuntimeException ex) {
            videoUploadEntity.setUploadStatus(VideoUploadStatus.FAILED);
            videoUploadEntity.setFailureReason(ex.getMessage());
            videoUploadRepository.save(videoUploadEntity);
            throw ex;
        }
    }

    @Override
    public AbortVideoMultipartUploadResponse abortMultipartUpload(Long idAlbum, String objectKey, String uploadId) {
        getAlbum(idAlbum);
        VideoUploadEntity videoUploadEntity = getVideoUpload(idAlbum, objectKey, uploadId);
        minioRepository.abortMultipartUpload(bucket, objectKey, uploadId);
        videoUploadEntity.setUploadStatus(VideoUploadStatus.FAILED);
        videoUploadEntity.setFailureReason("Upload abortado pelo usuario");
        videoUploadEntity = videoUploadRepository.save(videoUploadEntity);
        return AbortVideoMultipartUploadResponse.builder()
                .id(videoUploadEntity.getId())
                .idAlbum(idAlbum)
                .objectKey(objectKey)
                .uploadId(uploadId)
                .aborted(true)
                .uploadStatus(videoUploadEntity.getUploadStatus())
                .processingStatus(videoUploadEntity.getProcessingStatus())
                .failureReason(videoUploadEntity.getFailureReason())
                .updatedAt(videoUploadEntity.getUpdatedAt())
                .build();
    }

    @Override
    public List<VideoUploadResponse> listVideoUploads(Long idAlbum) {
        getAlbum(idAlbum);
        return videoUploadRepository.findAllByAlbumIdOrderByCreatedAtDesc(idAlbum)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AlbumEntity getAlbum(Long idAlbum) {
        return albumRepository.findById(idAlbum)
                .orElseThrow(() -> new NotFoundException("Not Found Album"));
    }

    private VideoUploadEntity getVideoUpload(Long idAlbum, String objectKey, String uploadId) {
        return videoUploadRepository.findByAlbumIdAndObjectKeyAndUploadId(idAlbum, objectKey, uploadId)
                .orElseThrow(() -> new NotFoundException("Not Found Video Upload"));
    }

    private String buildObjectKey(Long idAlbum, String fileName) {
        String normalizedFileName = fileName == null ? "video.bin" : fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return "albums/" + idAlbum + "/videos/" + UUID.randomUUID() + "-" + normalizedFileName;
    }

    private List<PresignedVideoUploadPartResponse> buildPresignedParts(Integer totalParts, String objectKey, String uploadId) {
        return java.util.stream.IntStream.rangeClosed(1, totalParts)
                .mapToObj(partNumber -> PresignedVideoUploadPartResponse.builder()
                        .partNumber(partNumber)
                        .url(minioRepository.getMultipartUploadUrl(bucket, objectKey, uploadId, partNumber))
                        .build())
                .toList();
    }

    private VideoUploadResponse toResponse(VideoUploadEntity entity) {
        return VideoUploadResponse.builder()
                .id(entity.getId())
                .idAlbum(entity.getAlbum().getId())
                .bucket(entity.getBucket())
                .objectKey(entity.getObjectKey())
                .uploadId(entity.getUploadId())
                .originalFileName(entity.getOriginalFileName())
                .contentType(entity.getContentType())
                .totalParts(entity.getTotalParts())
                .uploadStatus(entity.getUploadStatus())
                .processingStatus(entity.getProcessingStatus())
                .processingReference(entity.getProcessingReference())
                .thumbnailObjectKey(entity.getThumbnailObjectKey())
                .previewObjectKey(entity.getPreviewObjectKey())
                .rendition360ObjectKey(entity.getRendition360ObjectKey())
                .rendition720ObjectKey(entity.getRendition720ObjectKey())
                .dashManifestObjectKey(entity.getDashManifestObjectKey())
                .durationSeconds(entity.getDurationSeconds())
                .failureReason(entity.getFailureReason())
                .completedAt(entity.getCompletedAt())
                .processedAt(entity.getProcessedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .url(entity.getUploadStatus() == VideoUploadStatus.COMPLETED
                        ? minioRepository.getUrlFile(entity.getObjectKey(), entity.getBucket())
                        : null)
                .thumbnailUrl(entity.getThumbnailObjectKey() != null
                        ? minioRepository.getUrlFile(entity.getThumbnailObjectKey(), entity.getBucket())
                        : null)
                .previewUrl(entity.getPreviewObjectKey() != null
                        ? minioRepository.getUrlFile(entity.getPreviewObjectKey(), entity.getBucket())
                        : null)
                .rendition360Url(entity.getRendition360ObjectKey() != null
                        ? minioRepository.getUrlFile(entity.getRendition360ObjectKey(), entity.getBucket())
                        : null)
                .rendition720Url(entity.getRendition720ObjectKey() != null
                        ? minioRepository.getUrlFile(entity.getRendition720ObjectKey(), entity.getBucket())
                        : null)
                .dashManifestUrl(entity.getDashManifestObjectKey() != null
                        ? minioRepository.getUrlFile(entity.getDashManifestObjectKey(), entity.getBucket())
                        : null)
                .build();
    }
}
