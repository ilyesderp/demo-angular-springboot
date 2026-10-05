package com.example.backend.application.port.in;

import java.util.UUID;

public interface AuthUseCase {

    AuthResult register(RegisterCommand command);

    AuthResult login(LoginCommand command);

    record RegisterCommand(String workspaceName, String email, String password) {}

    record LoginCommand(String email, String password) {}

    record AuthResult(String token, UUID userId, UUID workspaceId, String email) {}
}
