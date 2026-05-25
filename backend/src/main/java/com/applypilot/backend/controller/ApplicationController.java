package com.applypilot.backend.controller;

import com.applypilot.backend.dto.ApplicationRequest;
import com.applypilot.backend.dto.ApplicationResponse;
import com.applypilot.backend.model.User;
import com.applypilot.backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ApplicationRequest request
    ) {
        return ResponseEntity.ok(applicationService.createApplication(currentUser, request));
    }

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getApplications(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(applicationService.getApplicationsByUser(currentUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(applicationService.getApplicationById(currentUser, id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ApplicationRequest request
    ) {
        return ResponseEntity.ok(applicationService.updateApplication(currentUser, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id
    ) {
        applicationService.deleteApplication(currentUser, id);
        return ResponseEntity.noContent().build();
    }
}