package com.zorvyn.financedashboard.exception;

import com.zorvyn.financedashboard.dto.ApiResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler providing consistent API responses.
 * All responses follow the ApiResponseDTO format with success, message, data, and timestamp.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        // Authentication & Authorization Exceptions
        
        /**
         * Handle authentication failures (401 Unauthorized)
         */
        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleUnauthorized(UnauthorizedException ex) {
                log.warn("Unauthorized access attempt: {}", ex.getMessage());

                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponseDTO.error(ex.getMessage()));
        }

        /**
         * Handle authorization failures (403 Forbidden)
         */
        @ExceptionHandler(ForbiddenException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleForbidden(ForbiddenException ex) {
                log.warn("Forbidden access attempt: {}", ex.getMessage());

                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(ApiResponseDTO.error(ex.getMessage()));
        }

        // Resource Exceptions

        /**
         * Handle resource not found (404)
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
                log.warn("Resource not found: {}", ex.getMessage());

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(ApiResponseDTO.error(ex.getMessage()));
        }

        /**
         * Handle duplicate resource (409 Conflict)
         */
        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleDuplicateResource(DuplicateResourceException ex) {
                log.warn("Duplicate resource: {}", ex.getMessage());

                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(ApiResponseDTO.error(ex.getMessage()));
        }

        // Validation Exceptions

        /**
         * Handle @Valid annotation validation errors on request body
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {
                
                Map<String, String> fieldErrors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

                log.warn("Validation failed for {} field(s): {}", fieldErrors.size(), fieldErrors.keySet());

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Validation failed", fieldErrors));
        }

        /**
         * Handle constraint violations (e.g., @Positive on query params)
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponseDTO<Map<String, String>>> handleConstraintViolation(
                        ConstraintViolationException ex) {
                
                Map<String, String> errors = ex.getConstraintViolations().stream()
                        .collect(Collectors.toMap(
                                v -> v.getPropertyPath().toString(),
                                v -> v.getMessage(),
                                (v1, v2) -> v1 + "; " + v2
                        ));

                log.warn("Constraint violation: {}", errors);

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error("Validation failed", errors));
        }

        /**
         * Handle missing required request parameters
         */
        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleMissingParams(
                        MissingServletRequestParameterException ex) {
                
                String message = String.format("Required parameter '%s' of type %s is missing",
                        ex.getParameterName(), ex.getParameterType());
                
                log.warn("Missing parameter: {}", ex.getParameterName());

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error(message));
        }

        // Bad Request Exceptions

        /**
         * Handle general bad request errors
         */
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleBadRequest(BadRequestException ex) {
                log.warn("Bad request: {}", ex.getMessage());

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error(ex.getMessage()));
        }

        /**
         * Handle malformed JSON or invalid enum values in request body
         */
        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleMessageNotReadable(
                        HttpMessageNotReadableException ex) {
                
                log.warn("Malformed request body: {}", ex.getMostSpecificCause().getMessage());

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error(
                                "Malformed JSON or invalid field value. Check enum values (role: ADMIN|ANALYST|VIEWER, status: ACTIVE|INACTIVE, type: INCOME|EXPENSE)"));
        }

        /**
         * Handle type mismatch (e.g., string passed for Long id)
         */
        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {
                
                String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                        ex.getValue(), ex.getName(),
                        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");

                log.warn("Type mismatch: {} for parameter {}", ex.getValue(), ex.getName());

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponseDTO.error(message));
        }

        // Catch-All Handler

        /**
         * Catch-all for unhandled exceptions
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponseDTO<Void>> handleGeneralException(Exception ex) {
                log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponseDTO.error("An unexpected error occurred. Please try again later."));
        }
}
