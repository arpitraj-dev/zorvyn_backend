package com.zorvyn.financedashboard.security;

import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.exception.UnauthorizedException;
import com.zorvyn.financedashboard.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Spring MVC Interceptor that extracts X-USER-ID header from requests
 * and loads the corresponding user from the database.
 * 
 * The authenticated user is stored in UserContext for access throughout
 * the request lifecycle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    public static final String USER_ID_HEADER = "X-USER-ID";

    private final UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                             Object handler) throws Exception {
        
        String userIdHeader = request.getHeader(USER_ID_HEADER);

        if (userIdHeader == null || userIdHeader.isBlank()) {
            log.warn("Missing {} header in request to {}", USER_ID_HEADER, request.getRequestURI());
            throw new UnauthorizedException("Missing " + USER_ID_HEADER + " header");
        }

        Long userId;
        try {
            userId = Long.parseLong(userIdHeader.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid {} header value: {}", USER_ID_HEADER, userIdHeader);
            throw new UnauthorizedException("Invalid " + USER_ID_HEADER + " header: must be a valid user ID");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for ID: {}", userId);
                    return new UnauthorizedException("User not found with ID: " + userId);
                });

        // Store user in thread-local context
        UserContext.setCurrentUser(user);
        log.debug("Authenticated user: {} (ID: {}, Role: {})", user.getName(), user.getId(), user.getRole());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // Clean up thread-local to prevent memory leaks
        UserContext.clear();
    }
}
