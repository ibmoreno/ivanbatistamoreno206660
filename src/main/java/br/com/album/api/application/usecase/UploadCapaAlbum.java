package br.com.album.api.application.usecase;

import br.com.album.api.presentation.controller.dto.CapaAlbumResponse;
import java.io.InputStream;
import java.util.List;

public interface UploadCapaAlbum {
    List<CapaAlbumResponse> execute(Long id, List<InputStream> capas);
}
