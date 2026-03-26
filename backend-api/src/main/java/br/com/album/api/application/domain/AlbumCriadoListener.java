package br.com.album.api.application.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumCriadoListener {

    private final SimpMessagingTemplate template;

    @EventListener
    public void onAlbumCriadoEvent(AlbumEvent albumEvent) {
        AlbumNotification album = AlbumNotification.fromAlbumEntity(albumEvent.getAlbum());
        template.convertAndSend("/topic/album-criado", album);
        log.info("Novo Album criado com sucesso: {}", album);
    }
}
