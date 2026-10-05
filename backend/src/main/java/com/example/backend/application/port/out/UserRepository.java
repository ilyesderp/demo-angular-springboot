package com.example.backend.application.port.out;

import com.example.backend.domain.User;
import java.util.Optional;

public interface UserRepository {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    User save(User user);
}
