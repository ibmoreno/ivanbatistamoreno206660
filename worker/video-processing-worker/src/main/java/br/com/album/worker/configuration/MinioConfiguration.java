package br.com.album.worker.configuration;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfiguration {

    @Bean
    MinioClient minioClient(@Value("${minio.endpoint}") String endpoint,
                            @Value("${minio.accesskey}") String accessKey,
                            @Value("${minio.secretkey}") String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
