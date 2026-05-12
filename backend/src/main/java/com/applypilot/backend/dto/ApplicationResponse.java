package com.applypilot.backend.dto;

import com.applypilot.backend.model.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationResponse {

    private Long id;

    private String company;

    private String position;

    private String location;

    private String jobUrl;

    private ApplicationStatus status;

    private LocalDate appliedDate;

    private String notes;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long userId;
}