package com.applypilot.backend.service;

import com.applypilot.backend.dto.ApplicationRequest;
import com.applypilot.backend.dto.ApplicationResponse;
import com.applypilot.backend.exception.ResourceNotFoundException;
import com.applypilot.backend.model.Application;
import com.applypilot.backend.model.ApplicationStatus;
import com.applypilot.backend.model.User;
import com.applypilot.backend.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public ApplicationResponse createApplication(User currentUser, ApplicationRequest request) {
        Application application = new Application(
                request.getCompany(),
                request.getPosition(),
                currentUser
        );

        applyRequestFields(application, request);

        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        } else {
            application.setStatus(ApplicationStatus.SAVED);
        }

        return mapToResponse(applicationRepository.save(application));
    }

    public List<ApplicationResponse> getApplicationsByUser(User currentUser) {
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ApplicationResponse getApplicationById(User currentUser, Long id) {
        return mapToResponse(findOwnedApplication(currentUser, id));
    }

    public ApplicationResponse updateApplication(User currentUser, Long id, ApplicationRequest request) {
        Application application = findOwnedApplication(currentUser, id);

        application.setCompany(request.getCompany());
        application.setPosition(request.getPosition());
        applyRequestFields(application, request);

        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }

        return mapToResponse(applicationRepository.save(application));
    }

    public void deleteApplication(User currentUser, Long id) {
        Application application = findOwnedApplication(currentUser, id);
        applicationRepository.delete(application);
    }

    private Application findOwnedApplication(User currentUser, Long id) {
        return applicationRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private void applyRequestFields(Application application, ApplicationRequest request) {
        application.setLocation(request.getLocation());
        application.setJobUrl(request.getJobUrl());
        application.setAppliedDate(request.getAppliedDate());
        application.setNotes(request.getNotes());
    }

    private ApplicationResponse mapToResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse();
        response.setId(application.getId());
        response.setCompany(application.getCompany());
        response.setPosition(application.getPosition());
        response.setLocation(application.getLocation());
        response.setJobUrl(application.getJobUrl());
        response.setStatus(application.getStatus());
        response.setAppliedDate(application.getAppliedDate());
        response.setNotes(application.getNotes());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        response.setUserId(application.getUser().getId());
        return response;
    }
}