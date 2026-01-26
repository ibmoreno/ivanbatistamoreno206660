package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.adapter.AlbumAdapter;
import br.com.album.api.application.usecase.CreateAlbum;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.ArtistaEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateAlbumImpl implements CreateAlbum {
    private final AlbumRepository albumRepository;

    @Override
    public AlbumResponse execute(CreateAlbumRequest createAlbumRequest) {
        AlbumEntity albumEntity = AlbumEntity.builder()
                .titulo(createAlbumRequest.getTitulo())
                .artistas(createAlbumRequest.getArtistas().stream()
                        .map(a -> ArtistaEntity.builder()
                                .nome(a.getNome())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
        AlbumEntity savedAlbumEntity = albumRepository.save(albumEntity);
        return AlbumAdapter.convertToResponse(savedAlbumEntity);
    }

}
