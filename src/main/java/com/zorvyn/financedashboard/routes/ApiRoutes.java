package com.zorvyn.financedashboard.routes;

/**
 * Centralized API route constants for the Finance Dashboard application.
 *
 * <pre>
 *   @RequestMapping(ApiRoutes.USERS)
 *   public class UserController { ... }
 * </pre>
 */
public final class ApiRoutes {

    private ApiRoutes() {
        // Prevent instantiation
    }

    // Base
    public static final String API_BASE = "/api";

    // User Routes
    public static final String USERS = "/users";
    public static final String USER_BY_ID = "/{id}";
}
