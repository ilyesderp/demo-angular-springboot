package com.example.backend.application.port.in;

import com.example.backend.domain.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InvoiceUseCase {

    Invoice create(UUID workspaceId, CreateInvoiceCommand command);

    List<Invoice> list(UUID workspaceId);

    record CreateInvoiceCommand(
            UUID clientId, String currency, LocalDate issueDate, LocalDate dueDate, List<ItemCommand> items) {}

    record ItemCommand(String description, int quantity, long unitPriceCents) {}
}
