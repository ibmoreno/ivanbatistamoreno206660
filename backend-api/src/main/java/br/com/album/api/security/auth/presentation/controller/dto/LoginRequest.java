package br.com.album.api.security.auth.presentation.controller.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Schema(description = "username do usuário", example = "user")
    @NotBlank(message = "username é obrigatório")
    @NotNull(message = "username é obrigatório")
    private String username;

    @Schema(description = "senha do usuário", example = "user123")
    @NotBlank(message = "senha é obrigatório")
    @NotNull(message = "senha é obrigatório")
    private String password;

}
