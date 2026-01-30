package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponse {
    @Schema(description = "ID do album", example = "1")
    private Long id;
    @Schema(description = "Titulo do album", example = "“Bem Sertanejo")
    private String titulo;
    @Schema(description = "Artistas do album", example = "Michel Teló")
    private Set<ArtistaResponse> artistas;
    @Schema(description = "url da capa do album")
    private String urlCapa;
}
