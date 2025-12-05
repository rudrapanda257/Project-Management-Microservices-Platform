package com.taskmanager.user.service;

import com.taskmanager.user.dto.AuthResponse;
import com.taskmanager.user.dto.LoginRequest;
import com.taskmanager.user.dto.RegisterRequest;
import com.taskmanager.user.entity.User;
import com.taskmanager.user.exception.InvalidCredentialsException;
import com.taskmanager.user.exception.UserAlreadyExistsException;
import com.taskmanager.user.repository.UserRepository;
import com.taskmanager.user.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        
        // Parse and set role
        try {
            user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException e) {
            user.setRole(User.Role.MEMBER); // Default role
        }
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Generate token
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        
        // Return response
        return new AuthResponse(
            token,
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getName()
        );
    }
    
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Generate token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        // Return response
        return new AuthResponse(
            token,
            user.getEmail(),
            user.getRole().name(),
            user.getName()
        );
    }
}