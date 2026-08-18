package com.agentos.core.controller;

import com.agentos.core.dto.ErrorResponse;
import com.agentos.core.dto.LoginRequest;
import com.agentos.core.dto.LoginResponse;
import com.agentos.core.dto.RegisterRequest;
import com.agentos.core.entity.Tenant;
import com.agentos.core.entity.User;
import com.agentos.core.security.AgentosUserPrincipal;
import com.agentos.core.security.JwtService;
import com.agentos.core.service.AuditService;
import com.agentos.core.service.TenantService;
import com.agentos.core.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final TenantService tenantService;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthController(UserService userService, TenantService tenantService,
                          JwtService jwtService, AuditService auditService) {
        this.userService = userService;
        this.tenantService = tenantService;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        var userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new ErrorResponse(401, "Invalid credentials"));
        }
        User user = userOpt.get();
        if (!user.isActive()) {
            return ResponseEntity.status(401).body(new ErrorResponse(401, "Account is disabled"));
        }
        var principal = new AgentosUserPrincipal(user.getId(), user.getTenantId(), user.getEmail(), user.getRole());
        String token = jwtService.generateToken(user.getId(), user.getTenantId(), user.getEmail(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(token);
        response.setExpiresIn(3600);
        response.setTenantId(user.getTenantId().toString());
        response.setUserId(user.getId().toString());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        auditService.record(user.getTenantId(), user.getId(), "human", "auth.login",
                "user", user.getId().toString(), Map.of("result", "success"));
        return ResponseEntity.ok(response);
    }

    /**
     * Bootstraps a new tenant and its first ADMIN user.
     *
     * <p>This endpoint is intentionally limited to tenant creation: it does not
     * accept a {@code tenantId} from the caller, so an anonymous request cannot
     * mint an admin inside an existing tenant. Additional users within a tenant
     * must be created by an authenticated admin.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "Email already registered"));
        }
        Tenant tenant = tenantService.provision(request.getTenantName());
        User user = userService.createTenantAdmin(tenant.getId(), request.getEmail(), request.getPassword());

        auditService.record(tenant.getId(), user.getId(), "human", "tenant.register",
                "tenant", tenant.getId().toString(), Map.of("email", user.getEmail()));
        return ResponseEntity.ok().build();
    }
}
