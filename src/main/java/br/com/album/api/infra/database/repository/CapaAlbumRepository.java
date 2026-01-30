package br.com.album.api.infra.database.repository;

import br.com.album.api.infra.database.jpa.AlbumEntity;
import br.com.album.api.infra.database.jpa.CapaAlbumEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapaAlbumRepository extends JpaRepository<CapaAlbumEntity, Long> {
    List<CapaAlbumEntity> findAllByAlbum(AlbumEntity album);
}
