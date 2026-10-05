package com.example.backend.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "invoice_items")
class InvoiceItemEntity {

    @Id
    UUID id;

    @Column(nullable = false)
    String description;

    @Column(nullable = false)
    int quantity;

    @Column(name = "unit_price_cents", nullable = false)
    long unitPriceCents;

    protected InvoiceItemEntity() {}

    InvoiceItemEntity(UUID id, String description, int quantity, long unitPriceCents) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
        this.unitPriceCents = unitPriceCents;
    }
}
