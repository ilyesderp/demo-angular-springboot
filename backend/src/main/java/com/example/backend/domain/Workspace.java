package com.example.backend.domain;

import java.util.UUID;

public record Workspace(UUID id, String name) {

    public Workspace {
        if (name == null || name.isBlank()) {
            throw new DomainException("Workspace name is required");
        }
    }

    public static Workspace create(String name) {
        return new Workspace(UUID.randomUUID(), name.trim());
    }
}
