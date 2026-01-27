package br.com.album.api.security.bucket4j;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RateLimitConfig {
    private static final int MAX_TOKENS = 10;
    public static Bucket newBucket() {
        return Bucket.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(MAX_TOKENS)
                                .refillIntervally(MAX_TOKENS, Duration.ofMinutes(1))
                                .build()
                ).build();
    }
}
