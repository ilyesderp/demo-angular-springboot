package com.example.backend.application.port.out;

import com.example.backend.domain.Client;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientRepository {

    Client save(Client client);

    List<Client> findAllByWorkspace(UUID workspaceId);

    Optional<Client> findByIdAndWorkspace(UUID id, UUID workspaceId);

    long countByWorkspace(UUID workspaceId);
}
