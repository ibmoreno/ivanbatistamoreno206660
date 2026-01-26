package br.com.album.api.presentation.controller.dto;

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
    @NotBlank(message = "Nome do artista é obrigatório")
    @NotNull(message = "Nome do artista é obrigatório")
    private String nome;
}
