package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllAlbum {
    Page<AlbumResponse> execute(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable);
}
