package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.GetAlbumById;
import br.com.album.api.config.TestPostgreSQLContainer;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GetAlbumByIdImplTest extends TestPostgreSQLContainer {

    @Autowired
    private AlbumRepository albumRepository;
    private GetAlbumById getAlbumById;

    @BeforeEach
    void setUp() {
        getAlbumById = new GetAlbumByIdImpl(albumRepository);
    }

    @Test
    void shouldReturnAlbumWhenGetAlbumByIdIsCalled() {

        // given
        Long albumId = 1L;

        // when
        AlbumResponse response = getAlbumById.execute(albumId);

        // then
        assertEquals(albumId, response.getId());

    }

    @Test
    void shouldThrowExceptionWhenAlbumIsNotFound() {

        // given
        Long albumId = -1L;

        // when
        var exception =  assertThrows(NotFoundException.class, () -> getAlbumById.execute(albumId));

        // then
        assertEquals("Album not found", exception.getMessage());

    }

}