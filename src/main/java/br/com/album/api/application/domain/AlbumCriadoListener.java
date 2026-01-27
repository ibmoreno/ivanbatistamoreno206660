package br.com.album.api.application.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlbumCriadoListener {

    private final SimpMessagingTemplate template;

    @EventListener
    public void onAlbumCriadoEvent(AlbumEvent albumEvent) {
        AlbumNotification album = AlbumNotification.fromAlbumEntity(albumEvent.getAlbum());
        template.convertAndSend("/topic/album-criado", album);
    }
}
