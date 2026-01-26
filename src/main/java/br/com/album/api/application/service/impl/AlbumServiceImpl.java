package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.AlbumService;
import br.com.album.api.application.usecase.CreateAlbum;
import br.com.album.api.application.usecase.FindAllAlbum;
import br.com.album.api.application.usecase.FindArtistaByName;
import br.com.album.api.application.usecase.GetAlbumById;
import br.com.album.api.application.usecase.UpdateAlbum;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final FindAllAlbum findAllAlbum;
    private final CreateAlbum createAlbum;
    private final GetAlbumById getAlbumById;
    private final UpdateAlbum updateAlbum;
    private final FindArtistaByName findArtistaByName;

    @Override
    public Page<AlbumResponse> findAll(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable) {
        return findAllAlbum.execute(findAllAlbumRequest, pageable);
    }

    @Override
    public AlbumResponse create(CreateAlbumRequest createAlbumRequest) {
        return createAlbum.execute(createAlbumRequest);
    }

    @Override
    public AlbumResponse getById(Long id) {
        return getAlbumById.execute(id);
    }

    @Override
    public AlbumResponse update(Long id, UpdateAlbumRequest updateAlbumRequest) {
        return updateAlbum.execute(id, updateAlbumRequest);
    }

    @Override
    public Page<ArtistaAlbumResponse> findArtistaByName(String nome, Pageable pageable) {
        return findArtistaByName.execute(nome, pageable);
    }
}
