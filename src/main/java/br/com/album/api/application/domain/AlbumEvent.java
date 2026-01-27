package br.com.album.api.application.domain;

import br.com.album.api.infra.database.jpa.AlbumEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumEvent {
    private AlbumEntity album;
}
