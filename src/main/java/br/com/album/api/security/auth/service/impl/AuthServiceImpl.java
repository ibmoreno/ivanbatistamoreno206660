package br.com.album.api.security.auth.service.impl;

import br.com.album.api.exception.UnauthorizedException;
import br.com.album.api.presentation.controller.dto.RefreshToken;
import br.com.album.api.presentation.controller.dto.TokenResponse;
import br.com.album.api.security.auth.service.AuthService;
import br.com.album.api.security.auth.service.JwtRefreshTokenService;
import br.com.album.api.security.auth.service.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtRefreshTokenService refreshTokenService;
    private final JwtTokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    public TokenResponse login(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String accessToken = tokenService.generateToken(auth);
        RefreshToken refreshToken = refreshTokenService.create(username);
        return new TokenResponse(accessToken, refreshToken.getToken());

    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(token -> userDetailsService.loadUserByUsername(token.getUsername()))
                .map(user -> {
                            Authentication auth = new UsernamePasswordAuthenticationToken(
                                    user.getUsername(), "", user.getAuthorities()
                            );

                            return new TokenResponse(
                                    tokenService.generateToken(auth),
                                    refreshToken
                            );

                        }
                ).orElseThrow(() -> new UnauthorizedException("Refresh token invalid!"));

    }

}
