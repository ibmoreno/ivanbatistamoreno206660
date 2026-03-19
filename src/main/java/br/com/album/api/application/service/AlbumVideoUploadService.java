package br.com.album.api.application.service;

import br.com.album.api.presentation.controller.dto.AbortVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.CompleteVideoMultipartUploadRequest;
import br.com.album.api.presentation.controller.dto.CompleteVideoMultipartUploadResponse;
import br.com.album.api.presentation.controller.dto.CreateVideoMultipartUploadRequest;
import br.com.album.api.presentation.controller.dto.CreateVideoMultipartUploadResponse;

public interface AlbumVideoUploadService {
    CreateVideoMultipartUploadResponse createMultipartUpload(Long idAlbum, CreateVideoMultipartUploadRequest request);

    CompleteVideoMultipartUploadResponse completeMultipartUpload(Long idAlbum, CompleteVideoMultipartUploadRequest request);

    AbortVideoMultipartUploadResponse abortMultipartUpload(Long idAlbum, String objectKey, String uploadId);
}
