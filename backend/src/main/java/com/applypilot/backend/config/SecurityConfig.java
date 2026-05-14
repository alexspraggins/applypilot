package com.applypilot.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for now so API requests are easier to test.
            // Later, you can revisit this for production security.
            .csrf(csrf -> csrf.disable())

            // Allow requests without forcing login.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/health", "/api/**").permitAll()
                .anyRequest().permitAll()
            )

            // Disable Spring Security's default login page.
            .formLogin(form -> form.disable())

            // Disable browser basic-auth popup.
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
