package br.com.album.api.presentation.controller.dto;

import br.com.album.api.infra.database.jpa.VideoProcessingStatus;
import br.com.album.api.infra.database.jpa.VideoUploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVideoMultipartUploadResponse {
    @Schema(description = "ID do registro de upload de video", example = "1")
    private Long id;
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "Chave final do objeto no bucket", example = "albums/1/videos/uuid-video.mp4")
    private String objectKey;
    @Schema(description = "UploadId retornado pelo MinIO")
    private String uploadId;
    @Schema(description = "Tamanho sugerido de cada parte em MB", example = "10")
    private Integer partSizeMb;
    @Schema(description = "Content-Type informado pelo frontend", example = "video/mp4")
    private String contentType;
    @Schema(description = "Status do upload")
    private VideoUploadStatus uploadStatus;
    @Schema(description = "Status do processamento futuro")
    private VideoProcessingStatus processingStatus;
    @Schema(description = "Data de criacao do registro")
    private LocalDateTime createdAt;
    @Builder.Default
    @Schema(description = "Lista das URLs assinadas para cada parte")
    private List<PresignedVideoUploadPartResponse> parts = new ArrayList<>();
}
