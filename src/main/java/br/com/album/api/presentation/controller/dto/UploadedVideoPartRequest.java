package br.com.album.api.presentation.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class UploadedVideoPartRequest {
    @Schema(description = "Numero sequencial da parte", example = "1")
    @NotNull(message = "partNumber e obrigatorio")
    @Min(value = 1, message = "partNumber deve ser maior que zero")
    @JsonProperty("partNumber")
    private Integer partNumber;

    @Schema(description = "ETag devolvido pelo MinIO ao subir a parte", example = "\"fba9dede5f27731c9771645a39863328\"")
    @NotBlank(message = "eTag e obrigatorio")
    @JsonProperty("eTag")
    @JsonAlias({"etag", "ETag"})
    private String eTag;
}
