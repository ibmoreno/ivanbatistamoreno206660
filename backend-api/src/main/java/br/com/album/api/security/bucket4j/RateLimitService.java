package br.com.album.api.security.bucket4j;

import io.github.bucket4j.Bucket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class RateLimitService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> RateLimitConfig.newBucket());
    }
}
