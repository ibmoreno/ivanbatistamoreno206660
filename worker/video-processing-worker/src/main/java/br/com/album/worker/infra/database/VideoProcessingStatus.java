package br.com.album.worker.infra.database;

public enum VideoProcessingStatus {
    NOT_SCHEDULED,
    PENDING,
    QUEUED,
    PROCESSING,
    PROCESSED,
    FAILED
}
