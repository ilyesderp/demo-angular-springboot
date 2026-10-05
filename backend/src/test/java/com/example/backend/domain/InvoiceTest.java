package com.example.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InvoiceTest {

    private static final LocalDate ISSUED = LocalDate.of(2026, 10, 1);

    private static Invoice invoice(LocalDate dueDate, List<InvoiceItem> items) {
        return Invoice.draft(UUID.randomUUID(), UUID.randomUUID(), "INV-0001", "eur", ISSUED, dueDate, items);
    }

    @Test
    void totalIsSumOfQuantityTimesUnitPriceInCents() {
        Invoice invoice = invoice(ISSUED.plusDays(30), List.of(
                InvoiceItem.create("Design", 2, 15_050),
                InvoiceItem.create("Hosting", 1, 999)));

        assertThat(invoice.totalCents()).isEqualTo(31_099);
    }

    @Test
    void startsAsDraftWithUppercaseCurrency() {
        Invoice invoice = invoice(ISSUED, List.of(InvoiceItem.create("Work", 1, 100)));

        assertThat(invoice.status()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(invoice.currency()).isEqualTo("EUR");
    }

    @Test
    void rejectsInvoiceWithoutItems() {
        assertThatThrownBy(() -> invoice(ISSUED, List.of()))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("at least one item");
    }

    @Test
    void rejectsDueDateBeforeIssueDate() {
        assertThatThrownBy(() -> invoice(ISSUED.minusDays(1), List.of(InvoiceItem.create("Work", 1, 100))))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Due date");
    }

    @Test
    void rejectsInvalidItems() {
        assertThatThrownBy(() -> InvoiceItem.create(" ", 1, 100)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> InvoiceItem.create("Work", 0, 100)).isInstanceOf(DomainException.class);
        assertThatThrownBy(() -> InvoiceItem.create("Work", 1, -1)).isInstanceOf(DomainException.class);
    }
}
