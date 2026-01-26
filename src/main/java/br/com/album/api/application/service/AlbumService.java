package br.com.album.api.application.service;

import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AlbumService {
    Page<AlbumResponse> findAll(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable);

    AlbumResponse create(CreateAlbumRequest createAlbumRequest);

    AlbumResponse getById(Long id);
}
