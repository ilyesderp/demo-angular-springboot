package com.example.backend.adapter.in.web.dto;

import com.example.backend.domain.Client;
import com.example.backend.domain.Invoice;
import com.example.backend.domain.InvoiceStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public final class Dtos {

    private Dtos() {}

    public record RegisterRequest(
            @NotBlank @Size(max = 200) String workspaceName,
            @NotBlank @Email @Size(max = 320) String email,
            // bcrypt only uses the first 72 bytes
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    public record CreateClientRequest(@NotBlank @Size(max = 200) String name, @Email @Size(max = 320) String email) {}

    public record ItemRequest(
            @NotBlank @Size(max = 500) String description,
            @Min(1) int quantity,
            @Min(0) long unitPriceCents) {}

    public record CreateInvoiceRequest(
            @NotNull UUID clientId,
            @Size(min = 3, max = 3) String currency,
            @NotNull LocalDate issueDate,
            @NotNull LocalDate dueDate,
            @NotEmpty List<@Valid ItemRequest> items) {}

    public record ClientResponse(UUID id, String name, String email) {

        public static ClientResponse from(Client client) {
            return new ClientResponse(client.id(), client.name(), client.email());
        }
    }

    public record ItemResponse(String description, int quantity, long unitPriceCents, long totalCents) {}

    public record InvoiceResponse(
            UUID id,
            UUID clientId,
            String number,
            InvoiceStatus status,
            String currency,
            LocalDate issueDate,
            LocalDate dueDate,
            long totalCents,
            List<ItemResponse> items) {

        public static InvoiceResponse from(Invoice invoice) {
            List<ItemResponse> items = invoice.items().stream()
                    .map(item -> new ItemResponse(
                            item.description(), item.quantity(), item.unitPriceCents(), item.totalCents()))
                    .toList();
            return new InvoiceResponse(
                    invoice.id(), invoice.clientId(), invoice.number(), invoice.status(), invoice.currency(),
                    invoice.issueDate(), invoice.dueDate(), invoice.totalCents(), items);
        }
    }
}
