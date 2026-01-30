package br.com.album.api.application.usecase.impl;

import br.com.album.api.application.usecase.SynchronizeRegionais;
import br.com.album.api.infra.database.jpa.RegionalEntity;
import br.com.album.api.infra.database.repository.RegionalRepository;
import br.com.album.api.infra.gateway.regional.RegionalClient;
import br.com.album.api.infra.gateway.regional.dto.RegionalResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SynchronizeRegionaisImpl implements SynchronizeRegionais {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RegionalRepository repository;
    private final RegionalClient regionalClient;

    @Override
    public void execute() {

        logger.info("Iniciando sincronização de regionais");
        List<RegionalResponse> regionaisEndpoint = regionalClient.getRegionais();
        if (regionaisEndpoint.isEmpty()) return;

        List<RegionalEntity> regionaisAtivasDB = repository.findByAtivoTrue();
        Map<Integer, RegionalEntity> regionaisAtivas = regionaisAtivasDB.stream()
                .collect(Collectors.toMap(RegionalEntity::getIdExterno, Function.identity()));

        for (RegionalResponse response : regionaisEndpoint) {
            RegionalEntity existis = regionaisAtivas.get(response.id());

            if (existis == null) {
                criaNovaRegional(response);
                continue;
            }

            if (!existis.getNome().equals(response.nome())) {
                inativaRegional(existis);
                criaNovaRegional(response);
            }

        }

        Set<Integer> regionaisEndpointIds = regionaisEndpoint.stream()
                .map(RegionalResponse::id).collect(Collectors.toSet());
        regionaisAtivasDB.forEach(regionalEntity ->
                this.inativaRegionalNaoDisponivelNoEndpoint(regionalEntity, regionaisEndpointIds));

        logger.info("Sincronização de regionais concluída");

   }

    private void inativaRegionalNaoDisponivelNoEndpoint(RegionalEntity regional, Set<Integer> idsRegionalEndpoint) {
        if (!idsRegionalEndpoint.contains(regional.getIdExterno())) {
            inativaRegional(regional);
        }
    }

    private void criaNovaRegional(RegionalResponse response) {
        RegionalEntity novaRegional = RegionalEntity.builder()
                .idExterno(response.id())
                .nome(response.nome())
                .ativo(true)
                .build();
        repository.save(novaRegional);
    }

    private void inativaRegional(RegionalEntity regional) {
        regional.setAtivo(false);
        repository.save(regional);
    }

}
