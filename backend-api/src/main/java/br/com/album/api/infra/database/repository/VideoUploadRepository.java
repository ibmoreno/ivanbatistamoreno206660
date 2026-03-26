package br.com.album.api.infra.database.repository;

import br.com.album.api.infra.database.jpa.VideoUploadEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoUploadRepository extends JpaRepository<VideoUploadEntity, Long> {
    Optional<VideoUploadEntity> findByAlbumIdAndObjectKeyAndUploadId(Long albumId, String objectKey, String uploadId);

    List<VideoUploadEntity> findAllByAlbumIdOrderByCreatedAtDesc(Long albumId);
}
