package br.com.album.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Open API Definition", version = "1.0.0", description = "Album API"))
@SpringBootApplication
public class AlbumApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlbumApiApplication.class, args);
    }

}
