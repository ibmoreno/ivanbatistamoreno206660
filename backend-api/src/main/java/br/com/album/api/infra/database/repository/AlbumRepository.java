package br.com.album.api.infra.database.repository;

import br.com.album.api.infra.database.jpa.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<AlbumEntity, Long>, JpaSpecificationExecutor<AlbumEntity> {
}
