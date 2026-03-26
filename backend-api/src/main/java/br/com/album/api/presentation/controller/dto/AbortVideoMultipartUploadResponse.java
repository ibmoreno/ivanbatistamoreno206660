package br.com.album.api.presentation.controller.dto;

import br.com.album.api.infra.database.jpa.VideoProcessingStatus;
import br.com.album.api.infra.database.jpa.VideoUploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbortVideoMultipartUploadResponse {
    @Schema(description = "ID do registro de upload de video", example = "1")
    private Long id;
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "Chave final do objeto no bucket", example = "albums/1/videos/uuid-video.mp4")
    private String objectKey;
    @Schema(description = "Identificador do multipart upload no MinIO", example = "MWM4NjI2NDMtOTU1OC00NDZiLTk0OWEtMzQ4ZjY3NDkzYzcxLjE3NDE2NTM0NDU5NTI4NTU4NDA")
    private String uploadId;
    @Schema(description = "Indica se o upload multipart foi abortado", example = "true")
    private Boolean aborted;
    @Schema(description = "Status final do upload")
    private VideoUploadStatus uploadStatus;
    @Schema(description = "Status do processamento futuro")
    private VideoProcessingStatus processingStatus;
    @Schema(description = "Motivo da falha")
    private String failureReason;
    @Schema(description = "Data da ultima atualizacao")
    private LocalDateTime updatedAt;
}
