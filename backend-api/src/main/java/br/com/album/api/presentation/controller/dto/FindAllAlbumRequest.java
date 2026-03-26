package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FindAllAlbumRequest {
    @Schema(description = "Titulo do album", example = "Album 1")
    private String titulo;
    @Schema(description = "ID do Artista", example = "1")
    private Long artistaId;
}