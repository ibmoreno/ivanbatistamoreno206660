package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.adapter.AlbumAdapter;
import br.com.album.api.application.usecase.UpdateAlbum;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.ArtistaEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;
import br.com.album.api.presentation.controller.dto.UpdateArtistaRequest;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateAlbumImpl implements UpdateAlbum {
    private final AlbumRepository albumRepository;

    @Override
    public AlbumResponse execute(Long id, UpdateAlbumRequest updateAlbumRequest) {

        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        entity.setTitulo(updateAlbumRequest.getTitulo());
        entity.setArtistas(updateArtista(updateAlbumRequest.getArtistas()));

        AlbumEntity updatedAlbum = albumRepository.save(entity);
        return AlbumAdapter.convertToResponse(updatedAlbum);

    }


    private Set<ArtistaEntity> updateArtista(Set<UpdateArtistaRequest> artistas) {
        return artistas.stream().map(request ->
                ArtistaEntity.builder()
                        .id(request.getId())
                        .nome(request.getNome())
                        .build()
        ).collect(Collectors.toSet());
    }

}
