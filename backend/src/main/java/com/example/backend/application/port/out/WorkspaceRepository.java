package com.example.backend.application.port.out;

import com.example.backend.domain.Workspace;
import java.util.UUID;

public interface WorkspaceRepository {

    Workspace save(Workspace workspace);

    /** Atomically reserves and returns the next invoice number for the workspace. */
    int nextInvoiceNumber(UUID workspaceId);
}
