package com.zorvyn.financedashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

    private boolean success;
    private String message;
    private T data;

    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();

    /**
     * Factory method for success responses with data.
     */
    public static <T> ApiResponseDTO<T> success(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Factory method for success responses without data.
     */
    public static <T> ApiResponseDTO<T> success(String message) {
        return ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * Factory method for error responses.
     */
    public static <T> ApiResponseDTO<T> error(String message) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Factory method for error responses with data (e.g., validation errors).
     */
    public static <T> ApiResponseDTO<T> error(String message, T data) {
        return ApiResponseDTO.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .build();
    }
}
