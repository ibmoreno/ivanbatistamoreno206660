package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;

public interface UpdateAlbum {
    AlbumResponse execute(Long id, UpdateAlbumRequest updateAlbumRequest);
}
