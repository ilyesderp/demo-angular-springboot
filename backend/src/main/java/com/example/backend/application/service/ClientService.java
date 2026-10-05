package com.example.backend.application.service;

import com.example.backend.application.port.in.ClientUseCase;
import com.example.backend.application.port.out.ClientRepository;
import com.example.backend.domain.Client;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientService implements ClientUseCase {

    private final ClientRepository clients;

    public ClientService(ClientRepository clients) {
        this.clients = clients;
    }

    @Override
    public Client create(UUID workspaceId, CreateClientCommand command) {
        return clients.save(Client.create(workspaceId, command.name(), command.email()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> list(UUID workspaceId) {
        return clients.findAllByWorkspace(workspaceId);
    }
}
