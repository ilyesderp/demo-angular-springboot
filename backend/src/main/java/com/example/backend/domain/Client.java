package com.example.backend.domain;

import java.util.UUID;

public record Client(UUID id, UUID workspaceId, String name, String email) {

    public Client {
        if (name == null || name.isBlank()) {
            throw new DomainException("Client name is required");
        }
    }

    public static Client create(UUID workspaceId, String name, String email) {
        return new Client(UUID.randomUUID(), workspaceId, name.trim(), email);
    }
}
