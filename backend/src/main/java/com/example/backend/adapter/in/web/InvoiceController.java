package com.example.backend.adapter.in.web;

import com.example.backend.adapter.in.web.dto.Dtos.CreateInvoiceRequest;
import com.example.backend.adapter.in.web.dto.Dtos.InvoiceResponse;
import com.example.backend.application.port.in.InvoiceUseCase;
import com.example.backend.application.port.in.InvoiceUseCase.CreateInvoiceCommand;
import com.example.backend.application.port.in.InvoiceUseCase.ItemCommand;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/invoices")
class InvoiceController {

    private static final String DEFAULT_CURRENCY = "EUR";

    private final InvoiceUseCase invoices;

    InvoiceController(InvoiceUseCase invoices) {
        this.invoices = invoices;
    }

    @GetMapping
    List<InvoiceResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return invoices.list(AuthenticatedUser.from(jwt).workspaceId()).stream()
                .map(InvoiceResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    InvoiceResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateInvoiceRequest request) {
        List<ItemCommand> items = request.items().stream()
                .map(item -> new ItemCommand(item.description(), item.quantity(), item.unitPriceCents()))
                .toList();
        String currency = request.currency() == null ? DEFAULT_CURRENCY : request.currency();
        var created = invoices.create(
                AuthenticatedUser.from(jwt).workspaceId(),
                new CreateInvoiceCommand(request.clientId(), currency, request.issueDate(), request.dueDate(), items));
        return InvoiceResponse.from(created);
    }
}
