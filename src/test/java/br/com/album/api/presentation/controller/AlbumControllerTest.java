package br.com.album.api.presentation.controller;

import br.com.album.api.application.service.AlbumService;
import br.com.album.api.exception.NotFoundException;
import br.com.album.api.exception.handler.RestExceptionHandler;
import br.com.album.api.presentation.controller.dto.AlbumResponse;
import br.com.album.api.presentation.controller.dto.ArtistaResponse;
import br.com.album.api.presentation.controller.dto.CreateAlbumRequest;
import br.com.album.api.presentation.controller.dto.CreateArtistaRequest;
import br.com.album.api.presentation.controller.dto.FindAllAlbumRequest;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    private AlbumService albumService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        albumService = mock(AlbumService.class);
        AlbumController albumController = new AlbumController(albumService);
        mockMvc = MockMvcBuilders.standaloneSetup(albumController)
                .setControllerAdvice(new RestExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    void shouldReturnAlbumsWhenFindAllAlbumIsCalledWithFilter() throws Exception {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        ArtistaResponse artistaResponse = ArtistaResponse.builder().id(1L).nome("Artista 1").build();
        AlbumResponse albumResponse = AlbumResponse.builder().id(1L).titulo("Album 1")
                .artistas(Set.of(artistaResponse)).build();

        Mockito.when(albumService.findAll(any(FindAllAlbumRequest.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(albumResponse), pageable, 1));

        // when
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/album")
                .queryParam("size", "10")
                .queryParam("page", "0")
                .queryParam("sort", "titulo,DESC")
                .accept(MediaType.APPLICATION_JSON)

        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content.size()").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.number").value(0));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.size").value(10));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(1));

        verify(albumService).findAll(any(FindAllAlbumRequest.class), any(Pageable.class));

    }

    @Test
    void shouldSalveAlbumWithSuccess() throws Exception {

        // given
        CreateAlbumRequest request = CreateAlbumRequest.builder()
                .titulo("Album 1")
                .artistas(Set.of(CreateArtistaRequest.builder().nome("Artista 1").build()))
                .build();

        AlbumResponse response = AlbumResponse.builder()
                .id(1L)
                .titulo("Album 1")
                .artistas(Set.of(ArtistaResponse.builder()
                        .id(1L)
                        .nome("Artista 1")
                        .build()))
                .build();

        Mockito.when(albumService.create(any(CreateAlbumRequest.class))).thenReturn(response);

        // when
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/album")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request))
        );

        // then
        result.andExpect(status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Album 1"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.artistas.size()").value(1));

    }


    @Test
    void shouldReturnAlbumsWhenFindByIdIsCalled() throws Exception {

        // given
        Long albumId = 1L;
        ArtistaResponse artistaResponse = ArtistaResponse.builder().id(1L).nome("Artista 1").build();
        AlbumResponse albumResponse = AlbumResponse.builder().id(1L).titulo("Album 1")
                .artistas(Set.of(artistaResponse)).build();

        Mockito.when(albumService.getById(anyLong())).thenReturn(albumResponse);

        // when
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/album/{id}", albumId)
                .accept(MediaType.APPLICATION_JSON)

        );

        // then
        result.andExpect(status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(albumId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.titulo").value("Album 1"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.artistas.size()").value(1));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.artistas.size()").value(1));

        verify(albumService).getById(anyLong());

    }


    @Test
    void shouldReturnNotFoundExceptionWhenNotFoundFindByIdIsCalled() throws Exception {

        // given
        Long albumId = -1L;

        Mockito.when(albumService.getById(anyLong()))
                .thenThrow(new NotFoundException("Not Found Album"));

        // when
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/album/{id}", albumId)
                .accept(MediaType.APPLICATION_JSON)

        );

        // then
        result.andExpect(status().isNotFound());
        verify(albumService).getById(anyLong());
    }

}