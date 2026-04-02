package com.zorvyn.financedashboard.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to allow analyst-level access (ADMIN and ANALYST roles).
 * Shorthand for @RequiresRole({Role.ADMIN, Role.ANALYST})
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AnalystAccess {
}
