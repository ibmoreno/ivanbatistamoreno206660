package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.AlbumService;
import br.com.album.api.application.usecase.FindAllAlbum;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final FindAllAlbum findAllAlbum;

    @Override
    public Page<AlbumResponse> findAll(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable) {
        return findAllAlbum.execute(findAllAlbumRequest, pageable);
    }
}
