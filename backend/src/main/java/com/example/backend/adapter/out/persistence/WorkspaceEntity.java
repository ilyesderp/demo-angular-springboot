package com.example.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "workspaces")
class WorkspaceEntity {

    @Id
    UUID id;

    @Column(nullable = false)
    String name;

    @Column(name = "next_invoice_number", nullable = false)
    int nextInvoiceNumber = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt = Instant.now();

    protected WorkspaceEntity() {}

    WorkspaceEntity(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
