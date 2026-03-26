package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.SynchronizeRegionais;
import br.com.album.api.config.TestPostgreSQLContainer;
import br.com.album.api.infra.database.jpa.RegionalEntity;
import br.com.album.api.infra.database.repository.RegionalRepository;
import br.com.album.api.infra.gateway.regional.RegionalClient;
import br.com.album.api.infra.gateway.regional.dto.RegionalResponse;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;


@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SynchronizeRegionaisImplTest extends TestPostgreSQLContainer {

    @Autowired
    private RegionalRepository repository;

    @MockitoBean
    private RegionalClient regionalClient;

    private SynchronizeRegionais synchronizeRegionais;

    @BeforeEach
    void setUp() {
        synchronizeRegionais = new SynchronizeRegionaisImpl(repository, regionalClient);
    }

    @Test
    void shouldSynchronizedRegionaisWhenExecuteIsCalled() {

        // given
        List<RegionalResponse> regionaisEndpoint = List.of(
                new RegionalResponse(1, "REGIONAL 01"),
                new RegionalResponse(2, "REGIONAL 02"),
                new RegionalResponse(3, "REGIONAL 03")
        );

        Mockito.when(regionalClient.getRegionais()).thenReturn(regionaisEndpoint);

        // when
        synchronizeRegionais.execute();
        List<RegionalEntity> regionaisDB = repository.findByAtivoTrue();

        // then
        Assertions.assertEquals(3, regionaisDB.size());

    }


    @Test
    void shouldSynchronizedRegionaisAndDeactivateNotExistsInEndpoint() {

        // given
        List<RegionalEntity> regionaisExistentes = List.of(
                RegionalEntity.builder().idExterno(1).nome("REGIONAL 01").build(),
                RegionalEntity.builder().idExterno(2).nome("REGIONAL 02").build(),
                RegionalEntity.builder().idExterno(3).nome("REGIONAL 03").build()
        );
        repository.saveAll(regionaisExistentes);
        List<RegionalResponse> regionaisEndpoint = List.of(
                new RegionalResponse(4, "REGIONAL 04")
        );

        Mockito.when(regionalClient.getRegionais()).thenReturn(regionaisEndpoint);

        // when
        synchronizeRegionais.execute();
        List<RegionalEntity> regionaisDB = repository.findByAtivoTrue();

        // then
        Assertions.assertEquals(1, regionaisDB.size());

    }

    @Test
    void shouldSynchronizedRegionaisAndDeactivateNotWhenChangeName() {

        // given
        List<RegionalEntity> regionaisExistentes = List.of(
                RegionalEntity.builder().idExterno(4).nome("NOVA REGIONAL 04").build()
        );
        repository.saveAll(regionaisExistentes);
        List<RegionalResponse> regionaisEndpoint = List.of(
                new RegionalResponse(4, "REGIONAL 04")
        );

        Mockito.when(regionalClient.getRegionais()).thenReturn(regionaisEndpoint);

        // when
        synchronizeRegionais.execute();
        List<RegionalEntity> regionaisDB = repository.findByAtivoTrue();

        // then
        Assertions.assertEquals(1, regionaisDB.size());

    }


}