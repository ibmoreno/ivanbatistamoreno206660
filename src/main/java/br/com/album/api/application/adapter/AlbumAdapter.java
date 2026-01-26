package br.com.album.api.application.adapter;

import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.ArtistaEntity;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.ArtistaResponse;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlbumAdapter {
    public static AlbumResponse convertToResponse(AlbumEntity entity) {
        return AlbumResponse.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .artistas(entity
                        .getArtistas()
                        .stream()
                        .map(AlbumAdapter::convertToResponse)
                        .collect(Collectors.toSet())
                ).build();
    }

    private static ArtistaResponse convertToResponse(ArtistaEntity entity) {
        return ArtistaResponse.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .build();
    }
}
