package br.com.album.api.presentation.controller;

import br.com.album.api.application.service.ArtistaService;
import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ArtistaController", description = "Artista API")
@RestController
@RequestMapping("/api/v1/artista")
@RequiredArgsConstructor
public class ArtistaController {

    private final ArtistaService artistaService;

    @Operation(summary = "Lista Artista com os seus albuns", security = @SecurityRequirement(name = "barerAuth"))
    @ApiResponse(responseCode = "200", description = "Artistas listados com sucesso")
    @GetMapping
    public ResponseEntity<Page<ArtistaAlbumResponse>> findArtistaByName(@RequestParam(required = false) String nome,
                                                                        @SortDefault(sort = {"nome"}, direction = Sort.Direction.ASC)
                                                                        @ParameterObject Pageable pageable) {
        Page<ArtistaAlbumResponse> responses = artistaService.findByName(nome, pageable);
        return ResponseEntity.ok(responses);
    }

}
