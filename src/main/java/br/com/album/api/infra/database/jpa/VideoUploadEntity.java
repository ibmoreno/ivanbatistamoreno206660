package br.com.album.api.infra.database.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "video_upload")
public class VideoUploadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_album", nullable = false)
    private AlbumEntity album;

    @Column(name = "bucket", nullable = false, length = 100)
    private String bucket;

    @Column(name = "object_key", nullable = false, length = 255, unique = true)
    private String objectKey;

    @Column(name = "upload_id", nullable = false, length = 255)
    private String uploadId;

    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "total_parts", nullable = false)
    private Integer totalParts;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 30)
    private VideoUploadStatus uploadStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false, length = 30)
    private VideoProcessingStatus processingStatus;

    @Column(name = "processing_reference", length = 255)
    private String processingReference;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
