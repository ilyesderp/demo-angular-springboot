package com.example.backend.application.port.out;

import com.example.backend.domain.Invoice;
import com.example.backend.domain.InvoiceStatus;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    List<Invoice> findAllByWorkspace(UUID workspaceId);

    long countByWorkspace(UUID workspaceId);

    Map<InvoiceStatus, Long> totalCentsByStatus(UUID workspaceId);
}
