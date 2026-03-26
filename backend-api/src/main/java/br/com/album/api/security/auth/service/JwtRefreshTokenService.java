package br.com.album.api.security.auth.service;

import br.com.album.api.security.auth.presentation.controller.dto.RefreshToken;
import java.util.Optional;

public interface JwtRefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken refreshToken);
    RefreshToken create(String username);
}
