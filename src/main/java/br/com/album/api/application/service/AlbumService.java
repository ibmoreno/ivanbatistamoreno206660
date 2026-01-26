package br.com.album.api.application.service;

import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AlbumService {
    Page<AlbumResponse> findAll(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable);

    AlbumResponse create(CreateAlbumRequest createAlbumRequest);

    AlbumResponse getById(Long id);

    AlbumResponse update(Long id, UpdateAlbumRequest updateAlbumRequest);

    Page<ArtistaAlbumResponse> findArtistaByName(String nome, Pageable pageable);
}
