package com.example.backend.application.port.out;

import java.util.UUID;

public interface TokenIssuer {

    String issue(UUID userId, UUID workspaceId);
}
