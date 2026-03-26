package br.com.album.api.presentation.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteVideoMultipartUploadRequest {
    @Schema(description = "Chave final do objeto no bucket", example = "albums/1/videos/uuid-video.mp4")
    @NotBlank(message = "objectKey e obrigatorio")
    @JsonProperty("objectKey")
    private String objectKey;

    @Schema(description = "UploadId retornado na criacao do multipart upload")
    @NotBlank(message = "uploadId e obrigatorio")
    @JsonProperty("uploadId")
    private String uploadId;

    @Valid
    @Builder.Default
    @NotEmpty(message = "parts e obrigatorio")
    @JsonProperty("parts")
    private List<UploadedVideoPartRequest> parts = new ArrayList<>();
}
