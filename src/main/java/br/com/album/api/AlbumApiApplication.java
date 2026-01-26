package br.com.album.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@OpenAPIDefinition(info = @Info(title = "Open API Definition", version = "1.0.0", description = "API REST - Artistas e Álbuns"))
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class AlbumApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlbumApiApplication.class, args);
    }

}
