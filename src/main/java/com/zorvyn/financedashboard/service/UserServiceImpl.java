package com.zorvyn.financedashboard.service;

import com.zorvyn.financedashboard.dto.UserRequestDTO;
import com.zorvyn.financedashboard.dto.UserResponseDTO;
import com.zorvyn.financedashboard.entity.User;
import com.zorvyn.financedashboard.exception.DuplicateResourceException;
import com.zorvyn.financedashboard.exception.ResourceNotFoundException;
import com.zorvyn.financedashboard.mapper.UserMapper;
import com.zorvyn.financedashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for User operations.
 * Contains business logic and comprehensive logging for all operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        log.info("Creating user - email: {}, role: {}", userRequestDTO.getEmail(), userRequestDTO.getRole());

        if (userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Failed to create user - Email already exists: {}", userRequestDTO.getEmail());
            throw new DuplicateResourceException(
                    "User with email '" + userRequestDTO.getEmail() + "' already exists");
        }

        User user = userMapper.toEntity(userRequestDTO);
        User savedUser = userRepository.save(user);

        log.info("User created successfully - id: {}, email: {}, role: {}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getRole());
        
        return userMapper.toResponseDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        log.info("Fetching all users");
        
        List<User> users = userRepository.findAll();
        
        log.info("Retrieved {} users", users.size());
        
        return users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        
        log.debug("Found user - id: {}, email: {}, role: {}", id, user.getEmail(), user.getRole());
        
        return userMapper.toResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO) {
        log.info("Updating user - id: {}, newEmail: {}, newRole: {}",
                id, userRequestDTO.getEmail(), userRequestDTO.getRole());

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Failed to update - User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(userRequestDTO.getEmail()) &&
                userRepository.existsByEmail(userRequestDTO.getEmail())) {
            log.warn("Failed to update user {} - Email already exists: {}", id, userRequestDTO.getEmail());
            throw new DuplicateResourceException(
                    "User with email '" + userRequestDTO.getEmail() + "' already exists");
        }

        // Log what's changing
        log.debug("Updating user {} - name: {} -> {}, email: {} -> {}, role: {} -> {}, status: {} -> {}",
                id,
                existingUser.getName(), userRequestDTO.getName(),
                existingUser.getEmail(), userRequestDTO.getEmail(),
                existingUser.getRole(), userRequestDTO.getRole(),
                existingUser.getStatus(), userRequestDTO.getStatus());

        existingUser.setName(userRequestDTO.getName());
        existingUser.setEmail(userRequestDTO.getEmail());
        existingUser.setRole(userRequestDTO.getRole());
        existingUser.setStatus(userRequestDTO.getStatus());

        User updatedUser = userRepository.save(existingUser);
        
        log.info("User updated successfully - id: {}", updatedUser.getId());
        
        return userMapper.toResponseDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.error("Failed to delete - User not found with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        
        log.info("User deleted successfully - id: {}", id);
    }
}
