package br.com.album.api.application.domain;

import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.ArtistaEntity;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumNotification {
    private Long id;
    private String titulo;
    private Set<String> artistas;

    public static AlbumNotification fromAlbumEntity(AlbumEntity entity) {
        return AlbumNotification.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .artistas(entity.getArtistas()
                        .stream()
                        .map(ArtistaEntity::getNome)
                        .collect(Collectors.toSet()))
                .build();
    }

}
