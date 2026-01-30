package br.com.album.api.infra.gateway.regional;

import br.com.album.api.infra.gateway.regional.dto.RegionalResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RegionalClient {

    private final RestTemplate restTemplate;
    private static final String PATH = "/v1/regionais";

    public List<RegionalResponse> getRegionais() {

        try {

            ResponseEntity<RegionalResponse[]> response =
                    restTemplate.getForEntity(PATH, RegionalResponse[].class);

            return Optional.ofNullable(response.getBody())
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());

        } catch (HttpStatusCodeException ex) {
            throw new IllegalStateException(
                    "Erro ao consumir API de regionais. Status: " + ex.getStatusCode(),
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException(
                    "Timeout ou erro de conexão ao consumir API de regionais",
                    ex
            );
        }

    }

}
