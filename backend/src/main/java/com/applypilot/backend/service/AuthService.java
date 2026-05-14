package com.applypilot.backend.service;

import com.applypilot.backend.dto.AuthResponse;
import com.applypilot.backend.dto.LoginRequest;
import com.applypilot.backend.dto.RegisterRequest;
import com.applypilot.backend.exception.BadRequestException;
import com.applypilot.backend.model.User;
import com.applypilot.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordHash
        );

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                "User registered successfully"
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new BadRequestException("Invalid email or password");
        }

        return new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                "Login successful"
        );
    }
}