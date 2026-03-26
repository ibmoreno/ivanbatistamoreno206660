package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.AlbumResponse;

public interface GetAlbumById {
    AlbumResponse execute(Long id);
}
