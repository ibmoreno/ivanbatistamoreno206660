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
public class CompleteVideoMultipartUploadResponse {
    @Schema(description = "ID do registro de upload de video", example = "1")
    private Long id;
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "Chave final do objeto no bucket", example = "albums/1/videos/uuid-video.mp4")
    private String objectKey;
    @Schema(description = "URL assinada para acesso ao video finalizado", example = "https://minio.com/album/1/video.mp4")
    private String url;
    @Schema(description = "Status do upload")
    private VideoUploadStatus uploadStatus;
    @Schema(description = "Status do processamento futuro")
    private VideoProcessingStatus processingStatus;
    @Schema(description = "Data de conclusao do upload")
    private LocalDateTime completedAt;
}
