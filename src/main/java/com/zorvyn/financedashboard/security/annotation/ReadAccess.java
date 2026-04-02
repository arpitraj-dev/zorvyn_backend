package com.zorvyn.financedashboard.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to allow read-only access (all authenticated roles).
 * Shorthand for @RequiresRole({Role.ADMIN, Role.ANALYST, Role.VIEWER})
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReadAccess {
}
