package com.example.backend.application.service;

import com.example.backend.application.exception.NotFoundException;
import com.example.backend.application.port.in.InvoiceUseCase;
import com.example.backend.application.port.out.ClientRepository;
import com.example.backend.application.port.out.InvoiceRepository;
import com.example.backend.application.port.out.WorkspaceRepository;
import com.example.backend.domain.Invoice;
import com.example.backend.domain.InvoiceItem;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceService implements InvoiceUseCase {

    private final InvoiceRepository invoices;
    private final ClientRepository clients;
    private final WorkspaceRepository workspaces;

    public InvoiceService(InvoiceRepository invoices, ClientRepository clients, WorkspaceRepository workspaces) {
        this.invoices = invoices;
        this.clients = clients;
        this.workspaces = workspaces;
    }

    @Override
    public Invoice create(UUID workspaceId, CreateInvoiceCommand command) {
        clients.findByIdAndWorkspace(command.clientId(), workspaceId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        List<InvoiceItem> items = command.items().stream()
                .map(item -> InvoiceItem.create(item.description(), item.quantity(), item.unitPriceCents()))
                .toList();
        String number = "INV-%04d".formatted(workspaces.nextInvoiceNumber(workspaceId));

        return invoices.save(Invoice.draft(
                workspaceId, command.clientId(), number, command.currency(),
                command.issueDate(), command.dueDate(), items));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> list(UUID workspaceId) {
        return invoices.findAllByWorkspace(workspaceId);
    }
}
