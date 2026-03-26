package br.com.album.api.security.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

public interface JwtTokenService {
    String generateToken(Authentication authentication);

    JwtDecoder getJwtDecoder();

    JwtEncoder getJwtEncoder();

}
