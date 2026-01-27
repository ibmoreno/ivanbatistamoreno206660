package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.UploadCapaAlbum;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.bucket.MinioRepository;
import br.com.album.api.infra.database.jpa.CapaAlbumEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.infra.database.repository.CapaAlbumRepository;
import br.com.album.api.presentation.controller.dto.CapaAlbumResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UploadCapaAlbumImpl implements UploadCapaAlbum {
    private final CapaAlbumRepository capaAlbumRepository;
    private final AlbumRepository albumRepository;
    private final MinioRepository minioRepository;

    @Value("${minio.bucketName}")
    private String bucket;

    @Override
    public List<CapaAlbumResponse> execute(Long idAlbum, List<InputStream> capas) {

        if (capas == null || capas.isEmpty()) {
            return List.of();
        }

        albumRepository.findById(idAlbum)
                .orElseThrow(() -> new NotFoundException("Not Found Album"));

        List<CapaAlbumResponse> capaAlbumResponses = new ArrayList<>();
        for (InputStream capaAlbum : capas) {
            String objectId = minioRepository.uploadFile(capaAlbum, bucket);
            CapaAlbumEntity capaAlbumEntity = CapaAlbumEntity.builder()
                    .idAlbum(idAlbum)
                    .bucket(bucket)
                    .hash(objectId)
                    .build();
            capaAlbumRepository.save(capaAlbumEntity);
            String url = minioRepository.getUrlFile(objectId, bucket);
            capaAlbumResponses.add(CapaAlbumResponse.builder()
                    .id(capaAlbumEntity.getId())
                    .idAlbum(idAlbum)
                    .url(url)
                    .build());
        }

        return capaAlbumResponses;

    }

}
