/*
    DEVELOPMENT TESTING ENDPOINT
    Add '/dev/**' to security filter chain to use
*/


/* 
package com.applypilot.backend.controller;

import com.applypilot.backend.model.Application;
import com.applypilot.backend.model.User;
import com.applypilot.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dev")
public class DevController {
    private final UserRepository userRepository;

    public DevController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/seed")
    public ResponseEntity<String> seedDatabase() {
        User user = new User (
            "Test User",
            "test@example.com",
            "fake-hased-password"
        );

        Application application = new Application(
                "Google",
                "Software Engineer",
                user
        );

        user.addApplication(application);
        
        userRepository.save(user);

        return ResponseEntity.ok("Seed data created sucessfully.");
    }

}
*/