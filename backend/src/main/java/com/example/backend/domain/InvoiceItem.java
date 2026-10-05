package com.example.backend.domain;

import java.util.UUID;

public record InvoiceItem(UUID id, String description, int quantity, long unitPriceCents) {

    public InvoiceItem {
        if (description == null || description.isBlank()) {
            throw new DomainException("Item description is required");
        }
        if (quantity < 1) {
            throw new DomainException("Item quantity must be at least 1");
        }
        if (unitPriceCents < 0) {
            throw new DomainException("Item unit price cannot be negative");
        }
    }

    public static InvoiceItem create(String description, int quantity, long unitPriceCents) {
        return new InvoiceItem(UUID.randomUUID(), description, quantity, unitPriceCents);
    }

    public long totalCents() {
        return Math.multiplyExact(unitPriceCents, (long) quantity);
    }
}
