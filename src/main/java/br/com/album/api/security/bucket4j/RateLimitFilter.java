package br.com.album.api.security.bucket4j;

import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitService rateLimitService;
    private static final AntPathMatcher matcher = new AntPathMatcher();
    private static final int NUM_TOKENS = 1;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return matcher.match("/actuator/**", path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        /**
         * TODO: implementar com JWT authentication
         * Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         * String userId = auth.getName(); // subject do JWT
         */
        String clientIp = request.getRemoteAddr();
        Bucket bucket = rateLimitService.getBucket(clientIp);
        if (bucket.tryConsume(NUM_TOKENS)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests, max 10 requests per minute");
        }

    }

}
