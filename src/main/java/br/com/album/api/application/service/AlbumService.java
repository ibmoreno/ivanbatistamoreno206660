package br.com.album.api.application.service;

import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AlbumService {
    Page<AlbumResponse> findAll(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable);
}
