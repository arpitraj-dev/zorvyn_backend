package com.zorvyn.financedashboard.security;

import com.zorvyn.financedashboard.entity.enums.Role;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.exception.UnauthorizedException;
import com.zorvyn.financedashboard.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter.
 * Validates JWT token from Authorization header and sets user context.
 * Falls back to X-USER-ID header for backward compatibility.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_HEADER = "X-USER-ID";

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Try JWT authentication first
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                authenticateWithJwt(jwt);
            } else {
                // Fall back to X-USER-ID header for backward compatibility
                authenticateWithUserIdHeader(request);
            }
        } catch (UnauthorizedException e) {
            // Clear context on authentication failure
            UserContext.clear();
            logger.warn("Authentication failed: {}", e.getMessage());
        } catch (Exception e) {
            UserContext.clear();
            logger.error("Unexpected error during authentication", e);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clear context after request
            UserContext.clear();
        }
    }

    /**
     * Extract JWT token from Authorization header.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Authenticate using JWT token.
     */
    private void authenticateWithJwt(String jwt) {
        Long userId = jwtUtil.getUserIdFromToken(jwt);
        String email = jwtUtil.getEmailFromToken(jwt);
        Role role = jwtUtil.getRoleFromToken(jwt);

        // Optionally verify user still exists and is active
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (!user.isActive()) {
            throw new UnauthorizedException("User account is inactive");
        }

        // Verify token role matches current user role (in case role changed)
        if (user.getRole() != role) {
            logger.warn("Token role mismatch for user {}: token={}, current={}", 
                    userId, role, user.getRole());
        }

        UserContext.setCurrentUser(user);
        logger.debug("JWT authentication successful for user: {} (ID: {})", email, userId);
    }

    /**
     * Authenticate using X-USER-ID header (backward compatibility).
     */
    private void authenticateWithUserIdHeader(HttpServletRequest request) {
        String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (!StringUtils.hasText(userIdHeader)) {
            return; // No authentication header present
        }

        try {
            Long userId = Long.parseLong(userIdHeader);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            if (!user.isActive()) {
                throw new UnauthorizedException("User account is inactive");
            }

            UserContext.setCurrentUser(user);
            logger.debug("X-USER-ID authentication successful for user ID: {}", userId);
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Invalid X-USER-ID format");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip authentication for public endpoints
        return path.startsWith("/auth/") || 
               path.equals("/") || 
               path.startsWith("/error") ||
               path.startsWith("/actuator");
    }
}
