package com.zorvyn.financedashboard.security;

import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.entity.enums.Role;
import com.zorvyn.financedashboard.exception.ForbiddenException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

/**
 * Centralized authorization service for role-based access control.
 * Provides methods to check user permissions based on their role.
 */
@Service
public class AuthorizationService {

    /**
     * Check if the current user has one of the required roles.
     * Throws ForbiddenException if the user does not have permission.
     *
     * @param action       Description of the action being performed (for error message)
     * @param allowedRoles Roles that are allowed to perform this action
     */
    public void requireRole(String action, Role... allowedRoles) {
        User user = UserContext.getCurrentUser();
        if (user == null) {
            throw new ForbiddenException("No authenticated user found");
        }

        Set<Role> allowed = Set.of(allowedRoles);
        if (!allowed.contains(user.getRole())) {
            throw new ForbiddenException(
                    String.format("Access denied: %s role cannot %s. Required roles: %s",
                            user.getRole(), action, Arrays.toString(allowedRoles)));
        }
    }

    /**
     * Check if current user is ADMIN.
     */
    public void requireAdmin(String action) {
        requireRole(action, Role.ADMIN);
    }

    /**
     * Check if current user can read data (all roles can read).
     */
    public void requireReadAccess(String action) {
        requireRole(action, Role.ADMIN, Role.ANALYST, Role.VIEWER);
    }

    /**
     * Check if current user can write data (only ADMIN).
     */
    public void requireWriteAccess(String action) {
        requireRole(action, Role.ADMIN);
    }

    /**
     * Check if current user can access summaries (ADMIN or ANALYST).
     */
    public void requireAnalystAccess(String action) {
        requireRole(action, Role.ADMIN, Role.ANALYST);
    }

    /**
     * Get the current authenticated user.
     */
    public User getCurrentUser() {
        return UserContext.getCurrentUser();
    }

    /**
     * Check if current user has a specific role.
     */
    public boolean hasRole(Role role) {
        User user = UserContext.getCurrentUser();
        return user != null && user.getRole() == role;
    }

    /**
     * Check if current user is ADMIN.
     */
    public boolean isAdmin() {
        return hasRole(Role.ADMIN);
    }

    /**
     * Check if current user is ANALYST.
     */
    public boolean isAnalyst() {
        return hasRole(Role.ANALYST);
    }

    /**
     * Check if current user is VIEWER.
     */
    public boolean isViewer() {
        return hasRole(Role.VIEWER);
    }
}
