package com.example.backend.application.port.in;

import com.example.backend.domain.Client;
import java.util.List;
import java.util.UUID;

public interface ClientUseCase {

    Client create(UUID workspaceId, CreateClientCommand command);

    List<Client> list(UUID workspaceId);

    record CreateClientCommand(String name, String email) {}
}
