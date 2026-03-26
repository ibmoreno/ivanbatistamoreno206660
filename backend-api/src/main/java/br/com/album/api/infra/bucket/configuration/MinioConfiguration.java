package br.com.album.api.infra.bucket.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accesskey}")
    private String accessKey;

    @Value("${minio.secretkey}")
    private String accessSecret;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, accessSecret)
                .build();
    }

}
