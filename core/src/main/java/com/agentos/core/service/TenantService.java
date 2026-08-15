package com.agentos.core.service;

import com.agentos.core.entity.Tenant;
import com.agentos.core.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Tenant lifecycle and lookup service.
 *
 * <p>Tenant existence is validated here rather than in controllers so that the
 * "never trust a tenantId from an untrusted source" rule is enforced in one
 * place. Controllers should resolve the active tenant through this service
 * before persisting any tenant-scoped record.
 */
@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Optional<Tenant> findById(UUID id) {
        return tenantRepository.findById(id);
    }

    public Optional<Tenant> findActiveById(UUID id) {
        return findById(id).filter(t -> "active".equalsIgnoreCase(t.getStatus()));
    }

    @Transactional
    public Tenant provision(String name) {
        Tenant tenant = new Tenant();
        tenant.setName(name);
        tenant.setStatus("active");
        return tenantRepository.save(tenant);
    }
}
