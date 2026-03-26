package br.com.album.api.security.auth.presentation.controller;


import br.com.album.api.security.auth.presentation.controller.dto.LoginRequest;
import br.com.album.api.security.auth.presentation.controller.dto.RefreshTokenRequest;
import br.com.album.api.security.auth.presentation.controller.dto.TokenResponse;
import br.com.album.api.security.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AuthController", description = "Authentication API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Realiza a autenticação e retorna um token JWT", security = {
            @SecurityRequirement(name = "")
    })
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna o token"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualiza o Access Token e retorna um token JWT", security = {
            @SecurityRequirement(name = "")
    })
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Retorna o token"),
                    @ApiResponse(responseCode = "401", description = "Não autorizado"),
            }
    )
    @PostMapping("/refreshToken")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refreshToken(request.getToken());
        return ResponseEntity.ok(response);
    }

}
