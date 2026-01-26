package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.adapter.ArtistaAdapter;
import br.com.album.api.application.usecase.FindArtistaByName;
import br.com.album.api.infra.database.jpa.ArtistaEntity;
import br.com.album.api.infra.database.jpa.ArtistaEntity_;
import br.com.album.api.infra.database.repository.ArtistaRepository;
import br.com.album.api.presentation.controller.dto.ArtistaAlbumResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class FindArtistaByNameImpl implements FindArtistaByName {
    private final ArtistaRepository repository;

    @Override
    public Page<ArtistaAlbumResponse> execute(String nome, Pageable pageable) {
        Specification<ArtistaEntity> spec = withTituloAlbum(nome);
        Page<ArtistaEntity> page = repository.findAll(spec, pageable);
        return page.map(ArtistaAdapter::convertToArtistaAlbumResponse);
    }

    private Specification<ArtistaEntity> withTituloAlbum(String nome) {
        if (!StringUtils.hasText(nome)) {
            return (root, query, criteriaBuilder) -> null;
        }

        String value = "%" + nome.toLowerCase() + "%";
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(ArtistaEntity_.nome)), value);
    }

}
