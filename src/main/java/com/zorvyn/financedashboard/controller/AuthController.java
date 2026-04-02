package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.ApiResponseDTO;
import com.zorvyn.financedashboard.dto.LoginRequestDTO;
import com.zorvyn.financedashboard.dto.LoginResponseDTO;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.exception.UnauthorizedException;
import com.zorvyn.financedashboard.repository.UserRepository;
import com.zorvyn.financedashboard.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication operations.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationMs;

    /**
     * POST /auth/login → Authenticate user and return JWT token
     * 
     * @param request Login request containing email
     * @return JWT token and user details
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {

        logger.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: {}", request.getEmail());
                    return new UnauthorizedException("Invalid credentials");
                });

        // Check if user is active
        if (!user.isActive()) {
            logger.warn("Login failed - user account is inactive: {}", request.getEmail());
            throw new UnauthorizedException("User account is inactive");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        LoginResponseDTO response = LoginResponseDTO.of(
                token,
                jwtExpirationMs / 1000, // Convert to seconds
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        logger.info("Login successful for user: {} (ID: {})", user.getEmail(), user.getId());

        return ResponseEntity.ok(
                ApiResponseDTO.success("Login successful", response)
        );
    }
}
