package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.FindAllAlbum;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.ArtistaResponse;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class FindAllAlbumImpl implements FindAllAlbum {

    private final AlbumRepository repository;

    @Override
    @Transactional
    public Page<AlbumResponse> execute(FindAllAlbumRequest findAllAlbumRequest, Pageable pageable) {
        Specification<AlbumEntity> spec = createSpecification(findAllAlbumRequest);
        Page<AlbumEntity> page = repository.findAll(spec, pageable);
        return convertToResponse(page);
    }

    private Specification<AlbumEntity> createSpecification(FindAllAlbumRequest findAllAlbumRequest) {
        if (findAllAlbumRequest == null || !StringUtils.hasText(findAllAlbumRequest.getTitulo())) {
            return (root, query, criteriaBuilder) -> null;
        }

        String titulo = "%" + findAllAlbumRequest.getTitulo().toLowerCase() + "%";
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("titulo")), titulo);
    }

    private Page<AlbumResponse> convertToResponse(Page<AlbumEntity> page) {
        return page.map(entity -> AlbumResponse.builder().id(entity.getId())
                .titulo(entity.getTitulo())
                .artistas(entity.getArtistas().stream().map(artistaEntity -> ArtistaResponse.builder()
                        .id(artistaEntity.getId())
                        .nome(artistaEntity.getNome())
                        .build()).collect(Collectors.toSet()))
                .build());
    }

}
