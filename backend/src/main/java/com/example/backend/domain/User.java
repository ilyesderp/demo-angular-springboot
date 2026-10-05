package com.example.backend.domain;

import java.util.UUID;

public record User(UUID id, UUID workspaceId, String email, String passwordHash) {

    public static User create(UUID workspaceId, String email, String passwordHash) {
        return new User(UUID.randomUUID(), workspaceId, email, passwordHash);
    }
}
