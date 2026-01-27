package br.com.album.api.infra.bucket;

import br.com.album.api.exception.ApiServiceApplicationException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public interface MinioRepository {
    String uploadFile(InputStream inputStream, String bucketName);

    String getUrlFile(String objectId, String bucketName);
}

@Component
@RequiredArgsConstructor
class MinioRepositoryImpl implements MinioRepository {

    private static final int EXPIRY_VALUE_MINUTES = 30;
    private final MinioClient minioClient;

    @Override
    public String uploadFile(InputStream inputStream, String bucketName) {

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            String objectId = UUID.randomUUID().toString();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectId)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType("image/jpeg")
                            .build());

            return objectId;

        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }

    }

    @Override
    public String getUrlFile(String objectId, String bucketName) {
        try {

            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectId)
                            .expiry(EXPIRY_VALUE_MINUTES, TimeUnit.MINUTES)
                            .build());

        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }

    }
}
