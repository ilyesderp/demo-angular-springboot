package com.example.backend.adapter.in.web;

import com.example.backend.adapter.in.web.dto.Dtos.LoginRequest;
import com.example.backend.adapter.in.web.dto.Dtos.RegisterRequest;
import com.example.backend.application.port.in.AuthUseCase;
import com.example.backend.application.port.in.AuthUseCase.AuthResult;
import com.example.backend.application.port.in.AuthUseCase.LoginCommand;
import com.example.backend.application.port.in.AuthUseCase.RegisterCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
class AuthController {

    private final AuthUseCase auth;

    AuthController(AuthUseCase auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AuthResult register(@Valid @RequestBody RegisterRequest request) {
        return auth.register(new RegisterCommand(request.workspaceName(), request.email(), request.password()));
    }

    @PostMapping("/login")
    AuthResult login(@Valid @RequestBody LoginRequest request) {
        return auth.login(new LoginCommand(request.email(), request.password()));
    }
}
