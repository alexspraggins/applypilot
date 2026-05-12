package com.applypilot.backend.service;

import com.applypilot.backend.dto.ApplicationRequest;
import com.applypilot.backend.dto.ApplicationResponse;
import com.applypilot.backend.model.Application;
import com.applypilot.backend.model.ApplicationStatus;
import com.applypilot.backend.model.User;
import com.applypilot.backend.repository.ApplicationRepository;
import com.applypilot.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.applypilot.backend.exception.ResourceNotFoundException;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    public ApplicationResponse createApplication(ApplicationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Application application = new Application(
                request.getCompany(),
                request.getPosition(),
                user
        );

        application.setLocation(request.getLocation());
        application.setJobUrl(request.getJobUrl());
        application.setAppliedDate(request.getAppliedDate());
        application.setNotes(request.getNotes());

        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        } else {
            application.setStatus(ApplicationStatus.SAVED);
        }

        Application savedApplication = applicationRepository.save(application);

        return mapToResponse(savedApplication);
    }

    public List<ApplicationResponse> getApplicationsByUserId(Long userId) {
        return applicationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        return mapToResponse(application);
    }

    public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        application.setCompany(request.getCompany());
        application.setPosition(request.getPosition());
        application.setLocation(request.getLocation());
        application.setJobUrl(request.getJobUrl());
        application.setAppliedDate(request.getAppliedDate());
        application.setNotes(request.getNotes());

        if (request.getStatus() != null) {
            application.setStatus(request.getStatus());
        }

        Application savedApplication = applicationRepository.save(application);

        return mapToResponse(savedApplication);
    }

    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found");
        }

        applicationRepository.deleteById(id);
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