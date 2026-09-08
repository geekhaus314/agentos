package com.agentos.core.controller;

import com.agentos.core.dto.ErrorResponse;
import com.agentos.core.dto.LoginRequest;
import com.agentos.core.dto.LoginResponse;
import com.agentos.core.entity.User;
import com.agentos.core.repository.UserRepository;
import com.agentos.core.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse(401, "Invalid credentials"));
        }
        User user = userOpt.get();
        if (!BCrypt.checkpw(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body(new ErrorResponse(401, "Invalid credentials"));
        }
        String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getEmail(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setExpiresIn(3600);
        response.setTenantId(user.getTenantId().toString());
        response.setUserId(user.getId().toString());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Email already registered"));
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setTenantId(request.getTenantId() != null ? UUID.fromString(request.getTenantId()) : UUID.randomUUID());
        user.setRole("ADMIN");
        user.setActive(true);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
