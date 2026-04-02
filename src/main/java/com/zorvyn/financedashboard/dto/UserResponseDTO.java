package com.zorvyn.financedashboard.dto;

import com.zorvyn.financedashboard.entity.enums.Role;
import com.zorvyn.financedashboard.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private Status status;
}
