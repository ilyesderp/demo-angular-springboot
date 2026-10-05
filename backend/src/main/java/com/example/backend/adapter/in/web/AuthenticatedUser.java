package com.example.backend.adapter.in.web;

import java.util.UUID;
import org.springframework.security.oauth2.jwt.Jwt;

/** The caller, as identified by the validated JWT. */
record AuthenticatedUser(UUID userId, UUID workspaceId) {

    static AuthenticatedUser from(Jwt jwt) {
        return new AuthenticatedUser(UUID.fromString(jwt.getSubject()), UUID.fromString(jwt.getClaimAsString("wid")));
    }
}
