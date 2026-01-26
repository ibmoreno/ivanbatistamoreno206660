package br.com.album.api.application.adapter;

import br.com.album.api.infra.database.jpa.ArtistaEntity;
import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import br.com.album.api.presentation.controller.dto.ArtistaResponse;
import java.net.URI;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistaAdapter {

    public static ArtistaResponse convertToResponse(ArtistaEntity entity) {
        return ArtistaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .build();
    }

    public static ArtistaAlbumResponse convertToArtistaAlbumResponse(ArtistaEntity entity) {
        URI locationAlbum = getLocationAlbum(entity.getId());
        return ArtistaAlbumResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .locationAlbum(locationAlbum.toString())
                .build();
    }

    private static URI getLocationAlbum(Long artistaId) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/album")
                .queryParam("artistaId", artistaId)
                .buildAndExpand(artistaId)
                .toUri();
    }

}
