package br.com.album.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ApiServiceApplicationException extends RuntimeException {
    public ApiServiceApplicationException(String message) {
        super(message);
    }
}
