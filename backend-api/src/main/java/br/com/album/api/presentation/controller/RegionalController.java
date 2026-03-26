package br.com.album.api.presentation.controller;

import br.com.album.api.application.service.RegionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "RegionalController", description = "Regional API")
@RestController
@RequestMapping("/api/v1/regional")
@RequiredArgsConstructor
public class RegionalController {
    private final RegionalService regionalService;

    @Operation(summary = "Importa e sincroniza dados das instituições regionais", security = @SecurityRequirement(name = "barerAuth"))
    @ApiResponse(responseCode = "200", description = "Regionais sincronizadas com sucesso")
    @GetMapping("/sync")
    public ResponseEntity<String> getAllRegionais() {
        regionalService.synchronize();
        return ResponseEntity.ok("regional synchronization process initiated");
    }

}