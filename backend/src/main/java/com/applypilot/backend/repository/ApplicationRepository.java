package com.applypilot.backend.repository;

import com.applypilot.backend.model.Application;
import com.applypilot.backend.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUserId(Long userId);

    List<Application> findByUserIdAndStatus(Long userId, ApplicationStatus status);

    List<Application> findByUserIdOrderByCreatedAtDesc(Long userId);
}
