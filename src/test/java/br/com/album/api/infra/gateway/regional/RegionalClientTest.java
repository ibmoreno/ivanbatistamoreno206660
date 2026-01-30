package br.com.album.api.infra.gateway.regional;

import br.com.album.api.infra.gateway.regional.config.RestTemplateConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@SpringBootTest(
        classes = {
                RegionalClient.class,
                RestTemplateConfig.class
        },
        properties = {
                "regional.api.base-url=http://localhost"
        }
)
@ImportAutoConfiguration(RestTemplateAutoConfiguration.class)
class RegionalClientTest {

    private MockRestServiceServer server;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RegionalClient regionalClient;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getRegionais() {

        server.expect(requestTo("http://localhost/v1/regionais"))
                .andRespond(withSuccess(
                        "[{ \"id\": 9, \"nome\": \"REGIONAL DE CUIABÁ\" }]",
                        MediaType.APPLICATION_JSON));

        var result = regionalClient.getRegionais();

        assertEquals(1, result.size());
        assertEquals("REGIONAL DE CUIABÁ", result.getFirst().nome());
    }


}