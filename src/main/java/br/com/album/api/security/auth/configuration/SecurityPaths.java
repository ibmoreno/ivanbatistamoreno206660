package br.com.album.api.security.auth.configuration;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityPaths {
    protected static final String[] WEBSOCKET = {
            "/ws/**"
    };
    protected static final String[] AUTHENTICATION = {
            "/api/v1/auth/**"
    };
    protected static final String[] ACTUATOR = {
            "/actuator/**"
    };
    protected static final String[] SWAGGER_UI = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
    protected static final String[] STATIC_FILES = {
            "/",
            "/favicon.ico",
            "/index.html",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**"
    };
}
