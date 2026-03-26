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
public class UpdateArtistaRequest {

    @Schema(description = "ID do artista", example = "1")
    @NotNull(message = "ID do artista é obrigatório")
    private Long id;

    @Schema(description = "Nome do artista", example = "Artista 1")
    @NotBlank(message = "Nome do artista é obrigatório")
    @NotNull(message = "Nome do artista é obrigatório")
    private String nome;
}
