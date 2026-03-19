package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AbortVideoMultipartUploadResponse {
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "Chave final do objeto no bucket", example = "albums/1/videos/uuid-video.mp4")
    private String objectKey;
    @Schema(description = "Identificador do multipart upload no MinIO", example = "MWM4NjI2NDMtOTU1OC00NDZiLTk0OWEtMzQ4ZjY3NDkzYzcxLjE3NDE2NTM0NDU5NTI4NTU4NDA")
    private String uploadId;
    @Schema(description = "Indica se o upload multipart foi abortado", example = "true")
    private Boolean aborted;
}
