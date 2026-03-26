package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;

public interface CreateAlbum {
    AlbumResponse execute(CreateAlbumRequest createAlbumRequest);
}
