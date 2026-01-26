package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistaResponse {
    @Schema(description = "ID do Artista", example = "1")
    private Long id;
    @Schema(description = "Nome do Artista", example = "Michel Teló")
    private String nome;
}