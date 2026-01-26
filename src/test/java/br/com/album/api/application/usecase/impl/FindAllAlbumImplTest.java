package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.FindAllAlbum;
import br.com.album.api.config.TestPostgreSQLContainer;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class FindAllAlbumImplTest extends TestPostgreSQLContainer {

    @Autowired
    private AlbumRepository albumRepository;

    private FindAllAlbum findAllAlbum;

    @BeforeEach
    void setUp() {
        findAllAlbum = new FindAllAlbumImpl(albumRepository);
    }

    @Test
    void shouldReturnAllAlbumsWhenFindAllAlbumIsCalled() {

        // given
        FindAllAlbumRequest request = new FindAllAlbumRequest();
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AlbumResponse> response = findAllAlbum.execute(request, pageable);

        // then
        assertEquals(10, response.getContent().size());

    }


    @Test
    void shouldReturnEmptyAlbumsWhenFindAllAlbumIsCalledWithFilter() {

        // given
        FindAllAlbumRequest request = new FindAllAlbumRequest();
        request.setTitulo("titulo");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AlbumResponse> response = findAllAlbum.execute(request, pageable);

        // then
        assertEquals(0, response.getContent().size());

    }
}