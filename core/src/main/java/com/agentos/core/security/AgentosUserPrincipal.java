package com.agentos.core.security;

import java.util.UUID;

public record AgentosUserPrincipal(UUID userId, UUID tenantId, String email, String role) {
}
