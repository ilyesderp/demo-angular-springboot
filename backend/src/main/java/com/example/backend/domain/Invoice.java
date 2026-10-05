package com.example.backend.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Invoice(
        UUID id,
        UUID workspaceId,
        UUID clientId,
        String number,
        InvoiceStatus status,
        String currency,
        LocalDate issueDate,
        LocalDate dueDate,
        List<InvoiceItem> items) {

    public Invoice {
        if (items == null || items.isEmpty()) {
            throw new DomainException("An invoice needs at least one item");
        }
        if (currency == null || currency.length() != 3) {
            throw new DomainException("Currency must be a 3-letter code");
        }
        if (dueDate.isBefore(issueDate)) {
            throw new DomainException("Due date cannot be before the issue date");
        }
        items = List.copyOf(items);
    }

    public static Invoice draft(
            UUID workspaceId,
            UUID clientId,
            String number,
            String currency,
            LocalDate issueDate,
            LocalDate dueDate,
            List<InvoiceItem> items) {
        return new Invoice(
                UUID.randomUUID(), workspaceId, clientId, number, InvoiceStatus.DRAFT,
                currency.toUpperCase(), issueDate, dueDate, items);
    }

    /** Total in minor units (cents). */
    public long totalCents() {
        return items.stream().mapToLong(InvoiceItem::totalCents).reduce(0L, Math::addExact);
    }
}
