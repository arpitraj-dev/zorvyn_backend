package com.zorvyn.financedashboard.security;

import com.zorvyn.financedashboard.entity.User;

/**
 * Thread-local holder for the current authenticated user.
 * Used to pass user context through the request lifecycle without
 * explicitly passing it through method parameters.
 */
public class UserContext {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    /**
     * Set the current authenticated user for this request thread.
     */
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    /**
     * Get the current authenticated user for this request thread.
     */
    public static User getCurrentUser() {
        return currentUser.get();
    }

    /**
     * Clear the current user context (should be called after request processing).
     */
    public static void clear() {
        currentUser.remove();
    }
}
