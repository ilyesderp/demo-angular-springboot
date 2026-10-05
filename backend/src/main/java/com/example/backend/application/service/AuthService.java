package com.example.backend.application.service;

import com.example.backend.application.exception.ConflictException;
import com.example.backend.application.exception.UnauthorizedException;
import com.example.backend.application.port.in.AuthUseCase;
import com.example.backend.application.port.out.PasswordHasher;
import com.example.backend.application.port.out.TokenIssuer;
import com.example.backend.application.port.out.UserRepository;
import com.example.backend.application.port.out.WorkspaceRepository;
import com.example.backend.domain.User;
import com.example.backend.domain.Workspace;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService implements AuthUseCase {

    private final WorkspaceRepository workspaces;
    private final UserRepository users;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    public AuthService(
            WorkspaceRepository workspaces,
            UserRepository users,
            PasswordHasher passwordHasher,
            TokenIssuer tokenIssuer) {
        this.workspaces = workspaces;
        this.users = users;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public AuthResult register(RegisterCommand command) {
        String email = normalize(command.email());
        if (users.existsByEmail(email)) {
            throw new ConflictException("Email is already registered");
        }
        Workspace workspace = workspaces.save(Workspace.create(command.workspaceName()));
        User user = users.save(User.create(workspace.id(), email, passwordHasher.hash(command.password())));
        return result(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(LoginCommand command) {
        User user = users.findByEmail(normalize(command.email()))
                .filter(found -> passwordHasher.matches(command.password(), found.passwordHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        return result(user);
    }

    private AuthResult result(User user) {
        return new AuthResult(
                tokenIssuer.issue(user.id(), user.workspaceId()), user.id(), user.workspaceId(), user.email());
    }

    private static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
