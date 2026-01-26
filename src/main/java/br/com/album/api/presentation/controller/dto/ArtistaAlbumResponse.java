package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArtistaAlbumResponse extends ArtistaResponse {
    @Schema(description = "URL para acessar os albuns do artista", example = "http://localhost:8080/api/v1/album?artistaId=1")
    private String locationAlbum;
}
