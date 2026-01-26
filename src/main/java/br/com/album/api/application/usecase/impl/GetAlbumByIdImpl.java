package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.adapter.AlbumAdapter;
import br.com.album.api.application.usecase.GetAlbumById;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAlbumByIdImpl implements GetAlbumById {
    private final AlbumRepository albumRepository;
    @Override
    public AlbumResponse execute(Long id) {
        AlbumEntity entity = albumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Album not found"));
        return AlbumAdapter.convertToResponse(entity);
    }
}
