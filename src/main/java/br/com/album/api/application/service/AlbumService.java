package br.com.album.api.application.service;

import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CapaAlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;
import java.io.InputStream;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface AlbumService {
    Page<AlbumResponse> findAll(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable);

    AlbumResponse create(CreateAlbumRequest createAlbumRequest);

    AlbumResponse getById(Long id);

    AlbumResponse update(Long id, UpdateAlbumRequest updateAlbumRequest);

    List<CapaAlbumResponse> uploadCapa(Long id, List<InputStream> capas);
}
