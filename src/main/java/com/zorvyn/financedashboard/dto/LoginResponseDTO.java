package com.zorvyn.financedashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response containing JWT token.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String email;
    private String role;

    public static LoginResponseDTO of(String token, Long expiresIn, Long userId, String email, String role) {
        return LoginResponseDTO.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .userId(userId)
                .email(email)
                .role(role)
                .build();
    }
}
