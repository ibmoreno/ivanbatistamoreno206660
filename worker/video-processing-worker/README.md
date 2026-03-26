# Video Processing Worker

Worker assíncrono para consumo da fila RabbitMQ e processamento de vídeo com FFmpeg.

## Responsabilidades

- consumir mensagens da fila `video.processing.queue`
- baixar o vídeo original do MinIO
- gerar thumbnail `.jpg`
- gerar preview `.mp4` de 10 segundos
- enviar artefatos de volta para o MinIO
- atualizar status de processamento no PostgreSQL

## Execução via Docker Compose

O serviço é iniciado pelo `docker-compose.yml` principal do repositório.

## Requisitos

- RabbitMQ ativo
- PostgreSQL ativo
- MinIO ativo
- bucket configurado
