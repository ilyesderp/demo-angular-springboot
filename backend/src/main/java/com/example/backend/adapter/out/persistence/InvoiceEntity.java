package com.example.backend.adapter.out.persistence;

import com.example.backend.domain.InvoiceStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
class InvoiceEntity {

    @Id
    UUID id;

    @Column(name = "workspace_id", nullable = false)
    UUID workspaceId;

    @Column(name = "client_id", nullable = false)
    UUID clientId;

    @Column(nullable = false)
    String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    InvoiceStatus status;

    @Column(nullable = false)
    String currency;

    @Column(name = "issue_date", nullable = false)
    LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    LocalDate dueDate;

    @Column(name = "total_cents", nullable = false)
    long totalCents;

    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt = Instant.now();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id", nullable = false)
    @OrderColumn(name = "position")
    List<InvoiceItemEntity> items = new ArrayList<>();

    protected InvoiceEntity() {}
}
