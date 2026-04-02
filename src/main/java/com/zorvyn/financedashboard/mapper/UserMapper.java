package com.zorvyn.financedashboard.mapper;

import com.zorvyn.financedashboard.dto.UserRequestDTO;
import com.zorvyn.financedashboard.dto.UserResponseDTO;
import com.zorvyn.financedashboard.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert UserRequestDTO → User entity
     */
    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .role(dto.getRole())
                .status(dto.getStatus())
                .build();
    }

    /**
     * Convert User entity → UserResponseDTO
     */
    public UserResponseDTO toResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .build();
    }
}
