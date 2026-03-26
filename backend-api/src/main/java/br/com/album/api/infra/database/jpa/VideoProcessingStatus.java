package br.com.album.api.infra.database.jpa;

public enum VideoProcessingStatus {
    NOT_SCHEDULED,
    PENDING,
    QUEUED,
    PROCESSING,
    PROCESSED,
    FAILED
}
