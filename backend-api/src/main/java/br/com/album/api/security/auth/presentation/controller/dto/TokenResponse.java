package br.com.album.api.security.auth.presentation.controller.dto;

public record TokenResponse(String accessToken, String refreshToken) {
}
