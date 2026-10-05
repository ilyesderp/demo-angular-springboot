package com.example.backend.adapter.out.security;

import com.example.backend.adapter.config.SecurityProperties;
import com.example.backend.application.port.out.TokenIssuer;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
class JwtTokenIssuer implements TokenIssuer {

    /** Claim carrying the workspace the user belongs to. */
    static final String WORKSPACE_CLAIM = "wid";

    private final JwtEncoder encoder;
    private final SecurityProperties properties;

    JwtTokenIssuer(JwtEncoder encoder, SecurityProperties properties) {
        this.encoder = encoder;
        this.properties = properties;
    }

    @Override
    public String issue(UUID userId, UUID workspaceId) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(userId.toString())
                .claim(WORKSPACE_CLAIM, workspaceId.toString())
                .issuedAt(now)
                .expiresAt(now.plus(properties.jwtTtl()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
