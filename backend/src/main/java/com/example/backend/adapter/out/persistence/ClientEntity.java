package com.example.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clients")
class ClientEntity {

    @Id
    UUID id;

    @Column(name = "workspace_id", nullable = false)
    UUID workspaceId;

    @Column(nullable = false)
    String name;

    String email;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt = Instant.now();

    protected ClientEntity() {}

    ClientEntity(UUID id, UUID workspaceId, String name, String email) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.name = name;
        this.email = email;
    }
}
