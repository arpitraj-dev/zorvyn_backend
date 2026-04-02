package com.zorvyn.financedashboard.security.aspect;

import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.entity.enums.Role;
import com.zorvyn.financedashboard.exception.ForbiddenException;
import com.zorvyn.financedashboard.security.UserContext;
import com.zorvyn.financedashboard.security.annotation.RequiresRole;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * Aspect that handles role-based authorization checks using annotations.
 * 
 * Processes the following annotations:
 * - @RequiresRole: Checks if user has one of the specified roles
 * - @AdminOnly: Checks if user is ADMIN
 * - @ReadAccess: Allows all authenticated users
 * - @AnalystAccess: Checks if user is ADMIN or ANALYST
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class RoleAuthorizationAspect {

    @Before("@annotation(requiresRole)")
    public void checkRequiresRole(JoinPoint joinPoint, RequiresRole requiresRole) {
        Role[] allowedRoles = requiresRole.value();
        checkAccess(joinPoint, allowedRoles);
    }

    @Before("@annotation(com.zorvyn.financedashboard.security.annotation.AdminOnly)")
    public void checkAdminOnly(JoinPoint joinPoint) {
        checkAccess(joinPoint, new Role[]{Role.ADMIN});
    }

    @Before("@annotation(com.zorvyn.financedashboard.security.annotation.ReadAccess)")
    public void checkReadAccess(JoinPoint joinPoint) {
        checkAccess(joinPoint, new Role[]{Role.ADMIN, Role.ANALYST, Role.VIEWER});
    }

    @Before("@annotation(com.zorvyn.financedashboard.security.annotation.AnalystAccess)")
    public void checkAnalystAccess(JoinPoint joinPoint) {
        checkAccess(joinPoint, new Role[]{Role.ADMIN, Role.ANALYST});
    }

    private void checkAccess(JoinPoint joinPoint, Role[] allowedRoles) {
        User user = UserContext.getCurrentUser();
        
        if (user == null) {
            throw new ForbiddenException("No authenticated user found");
        }

        Set<Role> allowed = Set.of(allowedRoles);
        if (!allowed.contains(user.getRole())) {
            String methodName = getMethodName(joinPoint);
            log.warn("Access denied for user {} (role: {}) attempting to access {}",
                    user.getId(), user.getRole(), methodName);
            
            throw new ForbiddenException(
                    String.format("Access denied: %s role cannot perform this action. Required roles: %s",
                            user.getRole(), Arrays.toString(allowedRoles)));
        }

        log.debug("Access granted for user {} (role: {}) to {}", 
                user.getId(), user.getRole(), getMethodName(joinPoint));
    }

    private String getMethodName(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        return method.getDeclaringClass().getSimpleName() + "." + method.getName();
    }
}
