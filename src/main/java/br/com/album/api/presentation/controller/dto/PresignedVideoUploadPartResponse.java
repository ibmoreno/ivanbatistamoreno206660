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
public class PresignedVideoUploadPartResponse {
    @Schema(description = "Numero sequencial da parte", example = "1")
    private Integer partNumber;
    @Schema(description = "URL assinada para upload direto dessa parte no MinIO")
    private String url;
}
