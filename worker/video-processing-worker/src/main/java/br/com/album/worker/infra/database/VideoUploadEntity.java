package br.com.album.worker.infra.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "video_upload")
public class VideoUploadEntity {

    @Id
    private Long id;

    @Column(name = "id_album", nullable = false)
    private Long albumId;

    @Column(name = "bucket", nullable = false)
    private String bucket;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "upload_id", nullable = false)
    private String uploadId;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "total_parts", nullable = false)
    private Integer totalParts;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false)
    private VideoUploadStatus uploadStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private VideoProcessingStatus processingStatus;

    @Column(name = "processing_reference")
    private String processingReference;

    @Column(name = "thumbnail_object_key")
    private String thumbnailObjectKey;

    @Column(name = "preview_object_key")
    private String previewObjectKey;

    @Column(name = "rendition_360_object_key")
    private String rendition360ObjectKey;

    @Column(name = "rendition_720_object_key")
    private String rendition720ObjectKey;

    @Column(name = "dash_manifest_object_key")
    private String dashManifestObjectKey;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
