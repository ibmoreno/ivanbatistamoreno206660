package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Builder.Default
    @Schema(description = "Lista das URLs assinadas para cada parte")
    private List<PresignedVideoUploadPartResponse> parts = new ArrayList<>();
}
