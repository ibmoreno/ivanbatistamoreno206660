package br.com.album.api.security.auth.service;

import br.com.album.api.security.auth.presentation.controller.dto.TokenResponse;

public interface AuthService {
     TokenResponse login(String username, String password);
     TokenResponse refreshToken(String refreshToken);
}
