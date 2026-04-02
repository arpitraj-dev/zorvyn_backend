package com.zorvyn.financedashboard.controller;

import com.zorvyn.financedashboard.dto.ApiResponseDTO;
import com.zorvyn.financedashboard.dto.UserRequestDTO;
import com.zorvyn.financedashboard.dto.UserResponseDTO;
import com.zorvyn.financedashboard.security.annotation.AdminOnly;
import com.zorvyn.financedashboard.security.annotation.ReadAccess;
import com.zorvyn.financedashboard.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * POST /users → Create a new user
     * Only ADMIN can create users
     */
    @PostMapping
    @AdminOnly
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);
        return new ResponseEntity<>(
                ApiResponseDTO.success("User created successfully", createdUser),
                HttpStatus.CREATED);
    }

    /**
     * GET /users → Get all users
     * All authenticated users can view
     */
    @GetMapping
    @ReadAccess
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(
                ApiResponseDTO.success("Users fetched successfully", users));
    }

    /**
     * GET /users/{id} → Get user by ID
     * All authenticated users can view
     */
    @GetMapping("/{id}")
    @ReadAccess
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success("User fetched successfully", user));
    }

    /**
     * PUT /users/{id} → Update user
     * Only ADMIN can update users
     */
    @PutMapping("/{id}")
    @AdminOnly
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
        return ResponseEntity.ok(
                ApiResponseDTO.success("User updated successfully", updatedUser));
    }

    /**
     * DELETE /users/{id} → Delete user
     * Only ADMIN can delete users
     */
    @DeleteMapping("/{id}")
    @AdminOnly
    public ResponseEntity<ApiResponseDTO<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(
                ApiResponseDTO.success("User deleted successfully"));
    }
}
