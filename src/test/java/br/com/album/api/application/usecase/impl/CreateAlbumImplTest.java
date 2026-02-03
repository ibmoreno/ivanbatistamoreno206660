package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.CreateAlbum;
import br.com.album.api.config.TestPostgreSQLContainer;
import br.com.album.api.infra.database.repository.AlbumRepository;
import br.com.album.api.infra.database.repository.ArtistaRepository;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.CreateArtistaRequest;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreateAlbumImplTest extends TestPostgreSQLContainer {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistaRepository artistaRepository;

    @MockitoBean
    private ApplicationEventPublisher eventPublisher;

    private CreateAlbum createAlbum;

    @BeforeEach
    void setUp() {
        createAlbum = new CreateAlbumImpl(albumRepository, artistaRepository, eventPublisher);
    }

    @Test
    void shouldCreateAlbumWhenCreateAlbumIsCalled() {

        // given
        CreateAlbumRequest request = CreateAlbumRequest
                .builder()
                .titulo("titulo")
                .artistas(Set.of(CreateArtistaRequest.builder()
                        .id(1L)
                        .nome("artista")
                        .build()))
                .build();


        // when
        AlbumResponse response = createAlbum.execute(request);

        // then
        assertEquals("titulo", response.getTitulo());
        assertEquals(1, response.getArtistas().size());

    }
}