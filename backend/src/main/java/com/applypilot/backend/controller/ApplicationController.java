package com.applypilot.backend.controller;

import com.applypilot.backend.dto.ApplicationRequest;
import com.applypilot.backend.dto.ApplicationResponse;
import com.applypilot.backend.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> createApplication(
            @Valid @RequestBody ApplicationRequest request
    ) {
        ApplicationResponse response = applicationService.createApplication(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByUserId(
            @RequestParam Long userId
    ) {
        List<ApplicationResponse> responses = applicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Long id
    ) {
        ApplicationResponse response = applicationService.getApplicationById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationRequest request
    ) {
        ApplicationResponse response = applicationService.updateApplication(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long id
    ) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}