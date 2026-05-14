package com.applypilot.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private Long userId;

    private String name;

    private String email;

    private String message;
}