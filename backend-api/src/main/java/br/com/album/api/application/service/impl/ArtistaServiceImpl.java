package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.ArtistaService;
import br.com.album.api.application.usecase.FindArtistaByName;
import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistaServiceImpl implements ArtistaService {
    private final FindArtistaByName findArtistaByName;

    @Override
    public Page<ArtistaAlbumResponse> findByName(String nome, Pageable pageable) {
        return findArtistaByName.execute(nome, pageable);
    }
}
