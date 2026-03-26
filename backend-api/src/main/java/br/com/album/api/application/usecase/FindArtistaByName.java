package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindArtistaByName {
    Page<ArtistaAlbumResponse> execute(String nome, Pageable pageable);
}
