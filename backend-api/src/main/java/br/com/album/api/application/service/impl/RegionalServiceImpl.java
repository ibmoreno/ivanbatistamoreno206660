package br.com.album.api.application.service.impl;

import br.com.album.api.application.service.RegionalService;
import br.com.album.api.application.usecase.SynchronizeRegionais;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionalServiceImpl implements RegionalService {
    private final SynchronizeRegionais synchronizeRegionais;

    @Override
    public void synchronize() {
        synchronizeRegionais.execute();
    }

}
