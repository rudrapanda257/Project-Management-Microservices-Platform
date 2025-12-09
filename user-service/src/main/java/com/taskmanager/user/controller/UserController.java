package com.taskmanager.user.controller;

import com.taskmanager.user.dto.UserDTO;
import com.taskmanager.user.entity.User;
import com.taskmanager.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserRepository userRepository;

    /**
     * Get user by ID - Used by other microservices via Feign
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) { // ✅ FIXED: Added "id" parameter name
        log.info("Request to get user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole().name());

        return ResponseEntity.ok(userDTO);
    }

    /**
     * Get user by email - Optional, useful for lookups
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) { // ✅ FIXED: Added "email"
                                                                                         // parameter name
        log.info("Request to get user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setRole(user.getRole().name());

        return ResponseEntity.ok(userDTO);
    }
}