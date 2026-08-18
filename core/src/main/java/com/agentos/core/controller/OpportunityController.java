package com.agentos.core.controller;

import com.agentos.core.dto.OpportunityRequest;
import com.agentos.core.entity.Opportunity;
import com.agentos.core.repository.OpportunityRepository;
import com.agentos.core.security.AgentosUserPrincipal;
import com.agentos.core.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/opportunities")
public class OpportunityController {

    private final OpportunityRepository opportunityRepository;
    private final AuditService auditService;

    public OpportunityController(OpportunityRepository opportunityRepository, AuditService auditService) {
        this.opportunityRepository = opportunityRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public List<Opportunity> list(@AuthenticationPrincipal AgentosUserPrincipal principal) {
        return opportunityRepository.findByTenantId(principal.tenantId());
    }

    @PostMapping
    public ResponseEntity<Opportunity> create(@AuthenticationPrincipal AgentosUserPrincipal principal,
                                              @Valid @RequestBody OpportunityRequest request) {
        Opportunity opp = new Opportunity();
        opp.setTenantId(principal.tenantId());
        opp.setSource(request.getSource());
        opp.setPriority(request.getPriority());
        opp.setEstimatedValue(request.getEstimatedValue());
        opp.setNotes(request.getNotes());
        opp.setStatus("new");
        opp.setQualificationStatus("new");
        Opportunity saved = opportunityRepository.save(opp);
        auditService.record(principal, "opportunity.create", "opportunity", saved.getId().toString(),
                Map.of("source", saved.getSource(), "priority", saved.getPriority()));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Opportunity> get(@AuthenticationPrincipal AgentosUserPrincipal principal, @PathVariable UUID id) {
        return opportunityRepository.findById(id)
                .filter(o -> o.getTenantId().equals(principal.tenantId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AgentosUserPrincipal principal, @PathVariable UUID id) {
        return opportunityRepository.findById(id)
                .filter(o -> o.getTenantId().equals(principal.tenantId()))
                .map(o -> {
                    opportunityRepository.delete(o);
                    auditService.record(principal, "opportunity.delete", "opportunity", o.getId().toString(), Map.of());
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
