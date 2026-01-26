package br.com.album.api.exception;


import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseError {
    private String title;
    private int status;
    private String message;
    private Map<String, String> details;
    private LocalDateTime timestamp;
}
