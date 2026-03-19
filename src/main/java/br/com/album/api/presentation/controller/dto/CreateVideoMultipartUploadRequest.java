package br.com.album.api.presentation.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class CreateVideoMultipartUploadRequest {
    @Schema(description = "Nome original do arquivo", example = "show-do-artista.mp4")
    @NotBlank(message = "fileName e obrigatorio")
    private String fileName;

    @Schema(description = "Content-Type do video", example = "video/mp4")
    @NotBlank(message = "contentType e obrigatorio")
    private String contentType;

    @Schema(description = "Quantidade de partes/chunks que o frontend vai enviar", example = "6")
    @NotNull(message = "totalParts e obrigatorio")
    @Min(value = 1, message = "totalParts deve ser maior que zero")
    @Max(value = 10000, message = "totalParts deve ser menor ou igual a 10000")
    private Integer totalParts;
}
