package br.com.album.worker.service;

import br.com.album.worker.domain.VideoProcessingMessage;
import br.com.album.worker.infra.database.VideoProcessingStatus;
import br.com.album.worker.infra.database.VideoUploadEntity;
import br.com.album.worker.infra.database.VideoUploadRepository;
import br.com.album.worker.infra.database.VideoUploadStatus;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessingWorkerService {

    private final VideoUploadRepository videoUploadRepository;
    private final MinioObjectStorageService minioObjectStorageService;
    private final FfmpegProcessingService ffmpegProcessingService;

    public void process(VideoProcessingMessage message) {
        VideoUploadEntity videoUpload = videoUploadRepository.findById(message.videoUploadId())
                .orElseThrow(() -> new IllegalStateException("Registro de upload nao encontrado: " + message.videoUploadId()));

        if (videoUpload.getUploadStatus() != VideoUploadStatus.COMPLETED) {
            log.warn("Upload {} ignorado porque ainda nao esta COMPLETED", videoUpload.getId());
            return;
        }

        updateStatus(videoUpload, VideoProcessingStatus.PROCESSING, null);

        Path workdir = null;
        try {
            workdir = Files.createTempDirectory("video-processing-");
            String originalExtension = extractExtension(videoUpload.getOriginalFileName(), ".mp4");
            Path sourceFile = workdir.resolve("source" + originalExtension);
            Path thumbnailFile = workdir.resolve("thumbnail.jpg");
            Path previewFile = workdir.resolve("preview.mp4");
            Path audioFile = workdir.resolve("audio.m4a");
            Path rendition360File = workdir.resolve("video-360p.mp4");
            Path rendition720File = workdir.resolve("video-720p.mp4");
            Path dashDirectory = Files.createDirectories(workdir.resolve("dash"));

            minioObjectStorageService.download(videoUpload.getBucket(), videoUpload.getObjectKey(), sourceFile);

            long durationSeconds = ffmpegProcessingService.probeDurationSeconds(sourceFile);
            ffmpegProcessingService.generateAudioTrack(sourceFile, audioFile);
            ffmpegProcessingService.generate360p(sourceFile, rendition360File);
            ffmpegProcessingService.generate720p(sourceFile, rendition720File);
            ffmpegProcessingService.generateThumbnail(sourceFile, thumbnailFile);
            ffmpegProcessingService.generatePreview(sourceFile, previewFile);
            ffmpegProcessingService.generateDash(rendition360File, rendition720File, audioFile, dashDirectory);

            String basePath = "albums/" + videoUpload.getAlbumId() + "/videos/derived/" + videoUpload.getId();
            String thumbnailObjectKey = basePath + "/thumbnail.jpg";
            String previewObjectKey = basePath + "/preview.mp4";
            String rendition360ObjectKey = basePath + "/video-360p.mp4";
            String rendition720ObjectKey = basePath + "/video-720p.mp4";
            String dashObjectPrefix = basePath + "/dash";
            String dashManifestObjectKey = dashObjectPrefix + "/manifest.mpd";

            minioObjectStorageService.upload(thumbnailFile, videoUpload.getBucket(), thumbnailObjectKey, "image/jpeg");
            minioObjectStorageService.upload(previewFile, videoUpload.getBucket(), previewObjectKey, "video/mp4");
            minioObjectStorageService.upload(rendition360File, videoUpload.getBucket(), rendition360ObjectKey, "video/mp4");
            minioObjectStorageService.upload(rendition720File, videoUpload.getBucket(), rendition720ObjectKey, "video/mp4");
            minioObjectStorageService.uploadDirectory(dashDirectory, videoUpload.getBucket(), dashObjectPrefix);

            videoUpload.setThumbnailObjectKey(thumbnailObjectKey);
            videoUpload.setPreviewObjectKey(previewObjectKey);
            videoUpload.setRendition360ObjectKey(rendition360ObjectKey);
            videoUpload.setRendition720ObjectKey(rendition720ObjectKey);
            videoUpload.setDashManifestObjectKey(dashManifestObjectKey);
            videoUpload.setDurationSeconds(durationSeconds);
            videoUpload.setFailureReason(null);
            videoUpload.setProcessedAt(LocalDateTime.now());
            videoUpload.setProcessingStatus(VideoProcessingStatus.PROCESSED);
            videoUploadRepository.save(videoUpload);
        } catch (Exception ex) {
            updateStatus(videoUpload, VideoProcessingStatus.FAILED, ex.getMessage());
            log.error("Falha ao processar video {}", videoUpload.getId(), ex);
        } finally {
            deleteRecursively(workdir);
        }
    }

    private void updateStatus(VideoUploadEntity videoUpload, VideoProcessingStatus status, String failureReason) {
        videoUpload.setProcessingStatus(status);
        videoUpload.setFailureReason(failureReason);
        videoUploadRepository.save(videoUpload);
    }

    private String extractExtension(String fileName, String fallback) {
        if (fileName == null || !fileName.contains(".")) {
            return fallback;
        }
        return fileName.substring(fileName.lastIndexOf('.'));
    }

    private void deleteRecursively(Path root) {
        if (root == null) {
            return;
        }
        try (var stream = Files.walk(root)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                            log.warn("Nao foi possivel remover arquivo temporario {}", path);
                        }
                    });
        } catch (Exception ignored) {
            log.warn("Nao foi possivel limpar diretorio temporario {}", root);
        }
    }
}
