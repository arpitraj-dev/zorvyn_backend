package com.zorvyn.financedashboard.security.annotation;

import com.zorvyn.financedashboard.entity.enums.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify required roles for accessing a controller method.
 * 
 * Usage:
 *   @RequiresRole(Role.ADMIN)                    - Single role
 *   @RequiresRole({Role.ADMIN, Role.ANALYST})    - Multiple roles (OR logic)
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresRole {
    
    /**
     * The roles that are allowed to access this endpoint.
     * If multiple roles are specified, user must have at least one of them (OR logic).
     */
    Role[] value();
}
