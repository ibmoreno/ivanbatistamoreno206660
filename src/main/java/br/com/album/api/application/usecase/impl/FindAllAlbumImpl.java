package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.adapter.AlbumAdapter;
import br.com.album.api.application.usecase.FindAllAlbum;
import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.AlbumEntity_;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
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
                criteriaBuilder.like(criteriaBuilder.lower(root.get(AlbumEntity_.titulo)), titulo);
    }

    private Page<AlbumResponse> convertToResponse(Page<AlbumEntity> page) {
        return page.map(AlbumAdapter::convertToResponse);
    }

}
