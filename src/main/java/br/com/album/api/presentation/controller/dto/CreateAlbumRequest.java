package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAlbumRequest {

    @Schema(description = "Titulo do album", example = "Album 1")
    @NotBlank(message = "Titulo do album é obrigatório")
    @NotNull(message = "Titulo do album é obrigatório")
    private String titulo;

    @Builder.Default
    @Schema(description = "Artistas do album", example = "Artista 1")
    @NotEmpty(message = "Artistas do album é obrigatório")
    private Set<CreateArtistaRequest> artistas = new HashSet<>();

}
