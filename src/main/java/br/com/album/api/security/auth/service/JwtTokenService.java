package br.com.album.api.security.auth.service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

public interface JwtTokenService {
    String generateToken(Authentication authentication);

    JwtDecoder getJwtDecoder();

    JwtEncoder getJwtEncoder();
}

@Service
class JwtTokenServiceImpl implements JwtTokenService {

    private static final Duration EXPIRATION = Duration.ofMinutes(5);

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;


    public JwtTokenServiceImpl(@Value("${jwt.secret}") String secret) {
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        this.jwtEncoder = new NimbusJwtEncoder(
                new ImmutableSecret<>(secretKey)
        );
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("ivanbatistamoreno206660-api")
                .issuedAt(now)
                .expiresAt(now.plus(EXPIRATION))
                .subject(authentication.getName())
                .claim("userId", "1")
                .claim("roles", authentication.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .build();
        return jwtEncoder.encode(
                JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)
        ).getTokenValue();
    }

    @Override
    public JwtDecoder getJwtDecoder() {
        return this.jwtDecoder;
    }

    @Override
    public JwtEncoder getJwtEncoder() {
        return this.jwtEncoder;
    }


}
