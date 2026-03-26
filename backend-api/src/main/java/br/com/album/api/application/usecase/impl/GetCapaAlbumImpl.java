package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.GetCapaAlbum;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.bucket.MinioRepository;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.CapaAlbumEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.infra.database.repository.CapaAlbumRepository;
import br.com.album.api.presentation.controller.dto.CapaAlbumResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCapaAlbumImpl implements GetCapaAlbum {
    private final CapaAlbumRepository capaAlbumRepository;
    private final MinioRepository minioRepository;
    private final AlbumRepository albumRepository;

    @Override
    public List<CapaAlbumResponse> execute(Long idAlbum) {

        AlbumEntity album = albumRepository.findById(idAlbum)
                .orElseThrow(() -> new NotFoundException("Album not found"));

        List<CapaAlbumEntity> capas = capaAlbumRepository.findAllByAlbum(album);
        if (capas.isEmpty()) {
            return List.of();
        }
        return capas.stream().map(capa -> CapaAlbumResponse.builder()
                .id(capa.getId())
                .idAlbum(capa.getAlbum().getId())
                .url(getUrlCapa(capa))
                .build()).toList();
    }

    private String getUrlCapa(CapaAlbumEntity capaAlbum) {
        return minioRepository.getUrlFile(capaAlbum.getHash(), capaAlbum.getBucket());
    }

}
