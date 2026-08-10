package com.agentos.core.repository;

import com.agentos.core.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {
    List<Lead> findByTenantId(UUID tenantId);
    List<Lead> findByTenantIdAndStatus(UUID tenantId, String status);
}
