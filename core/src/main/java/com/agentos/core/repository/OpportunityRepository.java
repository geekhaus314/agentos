package com.agentos.core.repository;

import com.agentos.core.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    List<Opportunity> findByTenantId(UUID tenantId);
    List<Opportunity> findByTenantIdAndStatus(UUID tenantId, String status);
}
