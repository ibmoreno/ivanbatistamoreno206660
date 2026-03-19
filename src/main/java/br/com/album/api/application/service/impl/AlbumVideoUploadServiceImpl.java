package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.AlbumVideoUploadService;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.bucket.MinioRepository;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AbortVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.CompleteVideoMultipartUploadRequest;
import br.com.album.api.presentation.controller.dto.CompleteVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.CreateVideoMultipartUploadRequest;
import br.com.album.api.presentation.controller.dto.CreateVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.PresignedVideoUploadPartResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumVideoUploadServiceImpl implements AlbumVideoUploadService {

    private final AlbumRepository albumRepository;
    private final MinioRepository minioRepository;

    @Value("${minio.bucketName}")
    private String bucket;

    @Value("${minio.multipart.partSizeMb:10}")
    private Integer partSizeMb;

    @Override
    public CreateVideoMultipartUploadResponse createMultipartUpload(Long idAlbum, CreateVideoMultipartUploadRequest request) {
        validateAlbumExists(idAlbum);
        String objectKey = buildObjectKey(idAlbum, request.getFileName());
        String uploadId = minioRepository.createMultipartUpload(bucket, objectKey);

        List<PresignedVideoUploadPartResponse> parts = buildPresignedParts(request.getTotalParts(), objectKey, uploadId);
        return CreateVideoMultipartUploadResponse.builder()
                .idAlbum(idAlbum)
                .objectKey(objectKey)
                .uploadId(uploadId)
                .partSizeMb(partSizeMb)
                .contentType(request.getContentType())
                .parts(parts)
                .build();
    }

    @Override
    public CompleteVideoMultipartUploadResponse completeMultipartUpload(Long idAlbum, CompleteVideoMultipartUploadRequest request) {
        validateAlbumExists(idAlbum);
        minioRepository.completeMultipartUpload(
                bucket,
                request.getObjectKey(),
                request.getUploadId(),
                request.getParts().stream()
                        .map(part -> new MinioRepository.MultipartUploadPart(part.getPartNumber(), part.getETag()))
                        .toList()
        );

        return CompleteVideoMultipartUploadResponse.builder()
                .idAlbum(idAlbum)
                .objectKey(request.getObjectKey())
                .url(minioRepository.getUrlFile(request.getObjectKey(), bucket))
                .build();
    }

    @Override
    public AbortVideoMultipartUploadResponse abortMultipartUpload(Long idAlbum, String objectKey, String uploadId) {
        validateAlbumExists(idAlbum);
        minioRepository.abortMultipartUpload(bucket, objectKey, uploadId);
        return AbortVideoMultipartUploadResponse.builder()
                .idAlbum(idAlbum)
                .objectKey(objectKey)
                .uploadId(uploadId)
                .aborted(true)
                .build();
    }

    private void validateAlbumExists(Long idAlbum) {
        if (!albumRepository.existsById(idAlbum)) {
            throw new NotFoundException("Not Found Album");
        }
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
}
