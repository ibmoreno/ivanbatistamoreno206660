package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.adapter.AlbumAdapter;
import br.com.album.api.application.domain.AlbumEvent;
import br.com.album.api.application.usecase.CreateAlbum;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.ArtistaEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.infra.database.repository.ArtistaRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.CreateArtistaRequest;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateAlbumImpl implements CreateAlbum {
    private final AlbumRepository albumRepository;
    private final ArtistaRepository artistaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public AlbumResponse execute(CreateAlbumRequest createAlbumRequest) {
        AlbumEntity albumEntity = AlbumEntity.builder()
                .titulo(createAlbumRequest.getTitulo())
                .artistas(createAlbumRequest.getArtistas().stream()
                        .map(this::findArtistaOrCreate)
                        .collect(Collectors.toSet()))
                .build();
        AlbumEntity savedAlbumEntity = albumRepository.save(albumEntity);
        log.info("Album criado com sucesso: {}", savedAlbumEntity);
        eventPublisher.publishEvent(new AlbumEvent(savedAlbumEntity));
        return AlbumAdapter.convertToResponse(savedAlbumEntity);
    }

    private ArtistaEntity findArtistaOrCreate(CreateArtistaRequest createArtistaRequest) {
        if (createArtistaRequest.getId() == null) {
            return ArtistaEntity.builder().nome(createArtistaRequest.getNome()).build();
        }
        return artistaRepository.findById(createArtistaRequest.getId())
                .orElseGet(() -> ArtistaEntity.builder()
                        .nome(createArtistaRequest.getNome())
                        .build());
    }

}
