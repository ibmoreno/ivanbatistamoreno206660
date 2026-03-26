package br.com.album.api.security.auth.permission;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityPaths {
    public static final String[] WEBSOCKET = {
            "/ws/**"
    };
    public static final String[] AUTHENTICATION = {
            "/api/v1/auth/**"
    };
    public static final String[] ACTUATOR = {
            "/actuator/**"
    };
    public static final String[] SWAGGER_UI = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
    public static final String[] STATIC_FILES = {
            "/",
            "/favicon.ico",
            "/index.html",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**"
    };
}
