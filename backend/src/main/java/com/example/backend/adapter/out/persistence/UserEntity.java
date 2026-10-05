package com.example.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
class UserEntity {

    @Id
    UUID id;

    @Column(name = "workspace_id", nullable = false)
    UUID workspaceId;

    @Column(nullable = false, unique = true)
    String email;

    @Column(name = "password_hash", nullable = false)
    String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt = Instant.now();

    protected UserEntity() {}

    UserEntity(UUID id, UUID workspaceId, String email, String passwordHash) {
        this.id = id;
        this.workspaceId = workspaceId;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
