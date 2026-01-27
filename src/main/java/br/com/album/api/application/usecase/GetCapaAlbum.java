package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.CapaAlbumResponse;
import java.util.List;

public interface GetCapaAlbum {
    List<CapaAlbumResponse> execute(Long idAlbum);
}
