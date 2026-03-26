package br.com.album.api.infra.bucket;

import br.com.album.api.exception.ApiServiceApplicationException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public interface MinioRepository {
    String uploadFile(InputStream inputStream, String bucketName);

    String getUrlFile(String objectId, String bucketName);

    String createMultipartUpload(String bucketName, String objectKey);

    String getMultipartUploadUrl(String bucketName, String objectKey, String uploadId, int partNumber);

    void completeMultipartUpload(String bucketName, String objectKey, String uploadId, List<MultipartUploadPart> parts);

    void abortMultipartUpload(String bucketName, String objectKey, String uploadId);

    record MultipartUploadPart(int partNumber, String eTag) {}
}

@Component
@RequiredArgsConstructor
class MinioRepositoryImpl implements MinioRepository {

    private static final int EXPIRY_VALUE_MINUTES = 30;
    private static final Pattern UPLOAD_ID_PATTERN = Pattern.compile("<UploadId>(.+?)</UploadId>");
    private final MinioClient minioClient;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Override
    public String uploadFile(InputStream inputStream, String bucketName) {

        try {
            ensureBucketExists(bucketName);
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

    @Override
    public String createMultipartUpload(String bucketName, String objectKey) {
        try {
            ensureBucketExists(bucketName);
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.POST)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(EXPIRY_VALUE_MINUTES, TimeUnit.MINUTES)
                            .extraQueryParams(Map.of("uploads", ""))
                            .build());

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            validateResponse(response, 200, "Erro ao iniciar multipart upload no MinIO");
            return extractUploadId(response.body());
        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }
    }

    @Override
    public String getMultipartUploadUrl(String bucketName, String objectKey, String uploadId, int partNumber) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(EXPIRY_VALUE_MINUTES, TimeUnit.MINUTES)
                            .extraQueryParams(Map.of(
                                    "uploadId", uploadId,
                                    "partNumber", String.valueOf(partNumber)
                            ))
                            .build());
        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }
    }

    @Override
    public void completeMultipartUpload(String bucketName, String objectKey, String uploadId, List<MultipartUploadPart> parts) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.POST)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(EXPIRY_VALUE_MINUTES, TimeUnit.MINUTES)
                            .extraQueryParams(Map.of("uploadId", uploadId))
                            .build());

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/xml")
                    .POST(HttpRequest.BodyPublishers.ofString(buildCompleteMultipartUploadXml(parts), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            validateResponse(response, 200, "Erro ao concluir multipart upload no MinIO");
        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }
    }

    @Override
    public void abortMultipartUpload(String bucketName, String objectKey, String uploadId) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.DELETE)
                            .bucket(bucketName)
                            .object(objectKey)
                            .expiry(EXPIRY_VALUE_MINUTES, TimeUnit.MINUTES)
                            .extraQueryParams(Map.of("uploadId", uploadId))
                            .build());

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .DELETE()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            validateResponse(response, 204, "Erro ao abortar multipart upload no MinIO");
        } catch (Exception e) {
            throw new ApiServiceApplicationException(e.getMessage());
        }
    }

    private void ensureBucketExists(String bucketName) throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private String extractUploadId(String body) {
        Matcher matcher = UPLOAD_ID_PATTERN.matcher(body);
        if (!matcher.find()) {
            throw new ApiServiceApplicationException("Nao foi possivel obter o uploadId do MinIO");
        }
        return matcher.group(1);
    }

    private String buildCompleteMultipartUploadXml(List<MultipartUploadPart> parts) {
        StringBuilder xml = new StringBuilder("<CompleteMultipartUpload>");
        parts.stream()
                .sorted((left, right) -> Integer.compare(left.partNumber(), right.partNumber()))
                .forEach(part -> xml.append("<Part>")
                        .append("<PartNumber>").append(part.partNumber()).append("</PartNumber>")
                        .append("<ETag>").append(normalizeETag(part.eTag())).append("</ETag>")
                        .append("</Part>"));
        xml.append("</CompleteMultipartUpload>");
        return xml.toString();
    }

    private String normalizeETag(String eTag) {
        String sanitized = eTag == null ? "" : eTag.trim().replace("\"", "");
        return "\"" + sanitized + "\"";
    }

    private void validateResponse(HttpResponse<String> response, int expectedStatus, String message) {
        if (response.statusCode() != expectedStatus) {
            throw new ApiServiceApplicationException(message + ": " + response.body());
        }
    }
}
