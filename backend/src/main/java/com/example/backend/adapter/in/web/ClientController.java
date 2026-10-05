package com.example.backend.adapter.in.web;

import com.example.backend.adapter.in.web.dto.Dtos.ClientResponse;
import com.example.backend.adapter.in.web.dto.Dtos.CreateClientRequest;
import com.example.backend.application.port.in.ClientUseCase;
import com.example.backend.application.port.in.ClientUseCase.CreateClientCommand;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
class ClientController {

    private final ClientUseCase clients;

    ClientController(ClientUseCase clients) {
        this.clients = clients;
    }

    @GetMapping
    List<ClientResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return clients.list(AuthenticatedUser.from(jwt).workspaceId()).stream()
                .map(ClientResponse::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ClientResponse create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateClientRequest request) {
        var created = clients.create(
                AuthenticatedUser.from(jwt).workspaceId(), new CreateClientCommand(request.name(), request.email()));
        return ClientResponse.from(created);
    }
}
