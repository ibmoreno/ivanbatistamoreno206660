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
public class CapaAlbumResponse {
    @Schema(description = "ID da capa", example = "1")
    private Long id;
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "URL da capa do album", example = "https://minio.com/album/1/capa.jpg")
    private String url;
}
