package com.zorvyn.financedashboard.dto;

import com.zorvyn.financedashboard.entity.enums.Role;
import com.zorvyn.financedashboard.entity.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    @NotNull(message = "Role is required (ADMIN, ANALYST, VIEWER)")
    private Role role;

    @NotNull(message = "Status is required (ACTIVE, INACTIVE)")
    private Status status;
}
