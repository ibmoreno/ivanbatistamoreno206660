package br.com.album.api.security.auth.filters;

import br.com.album.api.security.auth.dto.TokenResponse;
import br.com.album.api.security.auth.service.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String FORM_USERNAME_KEY = "username";
    private static final String FORM_PASSWORD_KEY = "password";

    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenService jwtTokenService) {
        this.setAuthenticationManager(authenticationManager);
        this.setUsernameParameter(FORM_USERNAME_KEY);
        this.setPasswordParameter(FORM_PASSWORD_KEY);
        this.setFilterProcessesUrl("/api/v1/auth/login");
        this.jwtTokenService = jwtTokenService;
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String token = jwtTokenService.generateToken(authResult);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), new TokenResponse(token));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("Credenciais inválidas");
    }

}
