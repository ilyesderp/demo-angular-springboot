package com.example.backend.application.service;

import com.example.backend.application.port.in.DashboardUseCase;
import com.example.backend.application.port.out.ClientRepository;
import com.example.backend.application.port.out.InvoiceRepository;
import com.example.backend.domain.InvoiceStatus;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService implements DashboardUseCase {

    private final InvoiceRepository invoices;
    private final ClientRepository clients;

    public DashboardService(InvoiceRepository invoices, ClientRepository clients) {
        this.invoices = invoices;
        this.clients = clients;
    }

    @Override
    public Summary summary(UUID workspaceId) {
        Map<InvoiceStatus, Long> totals = invoices.totalCentsByStatus(workspaceId);
        long outstanding = totals.getOrDefault(InvoiceStatus.SENT, 0L) + totals.getOrDefault(InvoiceStatus.OVERDUE, 0L);
        return new Summary(
                totals.getOrDefault(InvoiceStatus.DRAFT, 0L),
                outstanding,
                totals.getOrDefault(InvoiceStatus.PAID, 0L),
                invoices.countByWorkspace(workspaceId),
                clients.countByWorkspace(workspaceId));
    }
}
