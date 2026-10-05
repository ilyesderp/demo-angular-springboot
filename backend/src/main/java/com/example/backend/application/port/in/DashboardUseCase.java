package com.example.backend.application.port.in;

import java.util.UUID;

public interface DashboardUseCase {

    Summary summary(UUID workspaceId);

    record Summary(long draftCents, long outstandingCents, long paidCents, long invoiceCount, long clientCount) {}
}
