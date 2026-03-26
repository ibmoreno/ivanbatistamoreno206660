package br.com.album.worker.service;

import br.com.album.worker.domain.VideoProcessingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoProcessingRabbitListener {

    private final VideoProcessingWorkerService videoProcessingWorkerService;

    @RabbitListener(queues = "${video-processing.queue}")
    public void consume(VideoProcessingMessage message) {
        log.info("Mensagem recebida para processamento do video {}", message.videoUploadId());
        videoProcessingWorkerService.process(message);
    }
}
