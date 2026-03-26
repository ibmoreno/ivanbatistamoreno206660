package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.UpdateAlbum;
import br.com.album.api.config.TestPostgreSQLContainer;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.UpdateAlbumRequest;
import br.com.album.api.presentation.controller.dto.UpdateArtistaRequest;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UpdateAlbumImplTest extends TestPostgreSQLContainer {

    @Autowired
    private AlbumRepository albumRepository;
    private UpdateAlbum updateAlbum;

    @BeforeEach
    void setUp() {
        updateAlbum = new UpdateAlbumImpl(albumRepository);
    }

    @Test
    void shouldUpdateAlbumWhenUpdateAlbumIsCalled() {

        // given
        UpdateAlbumRequest request = UpdateAlbumRequest
                .builder()
                .titulo("titulo")
                .artistas(Set.of(UpdateArtistaRequest.builder()
                        .id(1L)
                        .nome("artista")
                        .build()))
                .build();

        // when
        AlbumResponse response = updateAlbum.execute(1L, request);

        // then
        assertEquals("titulo", response.getTitulo());
        assertEquals(1, response.getArtistas().size());

    }
}