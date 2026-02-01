package br.com.album.api.security.auth.service.impl;

import br.com.album.api.exception.UnauthorizedException;
import br.com.album.api.infra.database.jpa.RefreshTokenEntity;
import br.com.album.api.infra.database.repository.RefreshTokenRepository;
import br.com.album.api.presentation.controller.dto.RefreshToken;
import br.com.album.api.security.auth.service.JwtRefreshTokenService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtRefreshTokenServiceImpl implements JwtRefreshTokenService {
    private static final Duration REFRESH_TOKEN_DURATION = Duration.ofMinutes(30);
    private final RefreshTokenRepository repository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return repository.findByToken(token).map(
                entity -> RefreshToken
                        .builder()
                        .id(entity.getId())
                        .username(entity.getUsername())
                        .token(entity.getToken())
                        .expiresAt(entity.getExpiresAt())
                        .build()
        );
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            repository.deleteById(refreshToken.getId());
            throw new UnauthorizedException("Refresh token has expired.");
        }
        return refreshToken;
    }

    @Override
    public RefreshToken create(String username) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plus(REFRESH_TOKEN_DURATION))
                .build();
        repository.save(entity);
        return RefreshToken.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .token(entity.getToken())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

}
