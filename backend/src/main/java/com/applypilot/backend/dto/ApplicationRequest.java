package com.applypilot.backend.dto;

import com.applypilot.backend.model.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ApplicationRequest {

    @NotBlank(message = "Company is required")
    private String company;

    @NotBlank(message = "Position is required")
    private String position;

    private String location;

    private String jobUrl;

    private ApplicationStatus status;

    private LocalDate appliedDate;

    private String notes;
}