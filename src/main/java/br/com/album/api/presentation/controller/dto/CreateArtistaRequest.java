package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateArtistaRequest {
    @Schema(description = "Id do artista, caso já exista", example = "1")
    private Long id;
    @Schema(description = "Nome do artista", example = "Artista 1")
    @NotBlank(message = "Nome do artista é obrigatório")
    @NotNull(message = "Nome do artista é obrigatório")
    private String nome;
}
