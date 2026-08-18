package com.agentos.core.service;

import com.agentos.core.entity.AuditLog;
import com.agentos.core.repository.AuditLogRepository;
import com.agentos.core.security.AgentosUserPrincipal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Records audit entries for security-relevant and automated actions.
 *
 * <p>Strategy §35: every important automated action must be traceable by
 * timestamp, tenant, actor, action, resource, and result. Writes are async so
 * audit failures never block the request path; they are logged and swallowed
 * rather than propagated, since losing the business operation is worse than
 * losing the audit row.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    @Async
    public void record(UUID tenantId, UUID userId, String actorType, String action,
                       String resourceType, String resourceId, Map<String, Object> details) {
        try {
            AuditLog entry = new AuditLog();
            entry.setTenantId(tenantId);
            entry.setUserId(userId);
            entry.setAction(action);
            entry.setResourceType(resourceType);
            entry.setResourceId(resourceId);
            if (details != null) {
                entry.setDetails(objectMapper.writeValueAsString(details));
            }
            auditLogRepository.save(entry);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize audit details for action={}: {}", action, e.getMessage());
        } catch (Exception e) {
            log.warn("Failed to persist audit entry for action={}: {}", action, e.getMessage());
        }
    }

    public void record(AgentosUserPrincipal principal, String action,
                       String resourceType, String resourceId, Map<String, Object> details) {
        UUID tenantId = principal != null ? principal.tenantId() : null;
        UUID userId = principal != null ? principal.userId() : null;
        record(tenantId, userId, "human", action, resourceType, resourceId, details);
    }
}
