package br.com.album.worker.infra.database;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoUploadRepository extends JpaRepository<VideoUploadEntity, Long> {
    Optional<VideoUploadEntity> findByIdAndUploadStatus(Long id, VideoUploadStatus uploadStatus);
}
