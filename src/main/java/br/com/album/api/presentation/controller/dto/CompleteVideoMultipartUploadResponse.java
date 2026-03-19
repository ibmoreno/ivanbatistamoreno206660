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
public class CompleteVideoMultipartUploadResponse {
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "Chave final do objeto no bucket", example = "albums/1/videos/uuid-video.mp4")
    private String objectKey;
    @Schema(description = "URL assinada para acesso ao video finalizado", example = "https://minio.com/album/1/video.mp4")
    private String url;
}
