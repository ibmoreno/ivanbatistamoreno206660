package br.com.album.api.application.service;

import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistaService {
    Page<ArtistaAlbumResponse> findByName(String nome, Pageable pageable);
}
