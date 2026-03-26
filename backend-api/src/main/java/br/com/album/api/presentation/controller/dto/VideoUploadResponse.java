package br.com.album.api.presentation.controller.dto;

import br.com.album.api.infra.database.jpa.VideoProcessingStatus;
import br.com.album.api.infra.database.jpa.VideoUploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VideoUploadResponse {
    @Schema(description = "ID do registro de upload de video", example = "1")
    private Long id;
    @Schema(description = "ID do album", example = "1")
    private Long idAlbum;
    @Schema(description = "Bucket onde o video foi armazenado")
    private String bucket;
    @Schema(description = "Chave final do objeto no bucket")
    private String objectKey;
    @Schema(description = "UploadId do multipart upload")
    private String uploadId;
    @Schema(description = "Nome original do arquivo")
    private String originalFileName;
    @Schema(description = "Content-Type informado pelo cliente")
    private String contentType;
    @Schema(description = "Quantidade total de partes")
    private Integer totalParts;
    @Schema(description = "Status do upload")
    private VideoUploadStatus uploadStatus;
    @Schema(description = "Status do processamento futuro")
    private VideoProcessingStatus processingStatus;
    @Schema(description = "Referencia para pipeline futura")
    private String processingReference;
    @Schema(description = "Chave do thumbnail no bucket")
    private String thumbnailObjectKey;
    @Schema(description = "Chave do preview de 10 segundos no bucket")
    private String previewObjectKey;
    @Schema(description = "Chave da versao 360p no bucket")
    private String rendition360ObjectKey;
    @Schema(description = "Chave da versao 720p no bucket")
    private String rendition720ObjectKey;
    @Schema(description = "Chave do manifesto MPEG-DASH no bucket")
    private String dashManifestObjectKey;
    @Schema(description = "Duracao do video em segundos")
    private Long durationSeconds;
    @Schema(description = "Motivo de falha, quando houver")
    private String failureReason;
    @Schema(description = "Data de conclusao do upload")
    private LocalDateTime completedAt;
    @Schema(description = "Data de conclusao do processamento")
    private LocalDateTime processedAt;
    @Schema(description = "Data de criacao do registro")
    private LocalDateTime createdAt;
    @Schema(description = "Data da ultima atualizacao do registro")
    private LocalDateTime updatedAt;
    @Schema(description = "URL assinada para leitura do video")
    private String url;
    @Schema(description = "URL assinada do thumbnail")
    private String thumbnailUrl;
    @Schema(description = "URL assinada do preview de 10 segundos")
    private String previewUrl;
    @Schema(description = "URL assinada da versao 360p")
    private String rendition360Url;
    @Schema(description = "URL assinada da versao 720p")
    private String rendition720Url;
    @Schema(description = "URL assinada do manifesto DASH")
    private String dashManifestUrl;
}
