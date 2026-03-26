package br.com.album.worker.service;

import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinioObjectStorageService {

    private final MinioClient minioClient;

    public void download(String bucket, String objectKey, Path destination) throws Exception {
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectKey)
                        .filename(destination.toString())
                        .build()
        );
    }

    public void upload(Path file, String bucket, String objectKey, String contentType) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(file.toFile())) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(inputStream, file.toFile().length(), -1)
                            .contentType(contentType)
                            .build()
            );
        }
    }

    public void uploadDirectory(Path directory, String bucket, String objectKeyPrefix) throws Exception {
        try (var stream = Files.walk(directory)) {
            for (Path path : stream.sorted(Comparator.naturalOrder()).filter(Files::isRegularFile).toList()) {
                String relativePath = directory.relativize(path).toString().replace('\\', '/');
                String contentType = Files.probeContentType(path);
                upload(path, bucket, objectKeyPrefix + "/" + relativePath,
                        contentType != null ? contentType : "application/octet-stream");
            }
        }
    }
}
