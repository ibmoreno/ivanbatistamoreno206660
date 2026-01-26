package br.com.album.api.presentation.controller;

import br.com.album.api.application.service.AlbumService;
import br.com.album.api.exception.ApiServiceApplicationException;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CapaAlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "AlbumController", description = "Album API")
@RestController
@RequestMapping("/api/v1/album")
@RequiredArgsConstructor
@Validated
public class AlbumController {

    private final AlbumService albumService;

    @Operation(summary = "Lista todos os albums")
    @ApiResponse(responseCode = "200", description = "Albums listados com sucesso")
    @GetMapping
    public ResponseEntity<Page<AlbumResponse>> findAll(@ParameterObject FindAllAlbumRequest findAllAlbumRequest,
                                                       @SortDefault(sort = {"titulo"}, direction = Sort.Direction.ASC)
                                                       @ParameterObject Pageable pageable) {
        Page<AlbumResponse> responses = albumService.findAll(findAllAlbumRequest, pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Busca um album por ID")
    @ApiResponse(responseCode = "200", description = "Album encontrado com sucesso")
    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> findById(@PathVariable Long id) {
        AlbumResponse response = albumService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cria um album juntamente com os artistas")
    @ApiResponse(responseCode = "201", description = "Album criado com sucesso")
    @PostMapping
    public ResponseEntity<AlbumResponse> create(@Valid @RequestBody CreateAlbumRequest createAlbumRequest) {
        AlbumResponse response = albumService.create(createAlbumRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Atualiza dados de um album juntamente com os artistas")
    @ApiResponse(responseCode = "200", description = "Album atualizado com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateAlbumRequest updateAlbumRequest) {
        AlbumResponse response = albumService.update(id, updateAlbumRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Upload de capas de um album")
    @ApiResponse(responseCode = "200", description = "Capas de um album carregadas com sucesso")
    @PostMapping("/{id}/capa")
    public ResponseEntity<List<CapaAlbumResponse>> uploadCapa(@PathVariable Long id,
                                                              @Valid @RequestParam("files") MultipartFile[] files) {
        try {
            List<InputStream> capas = new ArrayList<>();
            for (MultipartFile file : files) {
                capas.add(file.getInputStream());
            }
            List<CapaAlbumResponse> response = albumService.uploadCapa(id, capas);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }
    }

}
