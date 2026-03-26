package br.com.album.api.application.service;

import org.springframework.scheduling.annotation.Async;

public interface RegionalService {
    @Async
    void synchronize();
}