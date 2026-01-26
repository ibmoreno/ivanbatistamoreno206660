package br.com.album.api.exception.handler;

import br.com.album.api.exception.NotFoundException;
import br.com.album.api.exception.ResponseError;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseError> handleNotFoundException(NotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseError> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ResponseError> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String error = String.format("Invalid value '%s' for parameter '%s'",
                ex.getValue(),
                ex.getName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, new RuntimeException(error));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "Validation error",
                        (existing, replacement) -> existing + ", " + replacement
                ));

        String errorMessage = "Validation failed for the following fields: " +
                String.join(", ", errors.keySet());

        ResponseError responseError = ResponseError.builder()
                .title("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .details(errors)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(responseError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseError> handleAllUncaughtException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ResponseError> buildErrorResponse(HttpStatus httpStatus, Exception ex) {
        ResponseError responseError = buildResponseError(httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(responseError);
    }

    private ResponseError buildResponseError(HttpStatus httpStatus, String detail) {
        return ResponseError.builder()
                .title(httpStatus.getReasonPhrase())
                .status(httpStatus.value())
                .message(detail)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
