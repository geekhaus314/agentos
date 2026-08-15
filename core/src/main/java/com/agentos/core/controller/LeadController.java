package com.agentos.core.controller;

import com.agentos.core.dto.LeadRequest;
import com.agentos.core.entity.Lead;
import com.agentos.core.repository.LeadRepository;
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
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadRepository leadRepository;
    private final AuditService auditService;

    public LeadController(LeadRepository leadRepository, AuditService auditService) {
        this.leadRepository = leadRepository;
        this.auditService = auditService;
    }

    @GetMapping
    public List<Lead> list(@AuthenticationPrincipal AgentosUserPrincipal principal) {
        return leadRepository.findByTenantId(principal.tenantId());
    }

    @PostMapping
    public ResponseEntity<Lead> create(@AuthenticationPrincipal AgentosUserPrincipal principal,
                                       @Valid @RequestBody LeadRequest request) {
        Lead lead = new Lead();
        lead.setTenantId(principal.tenantId());
        lead.setSource(request.getSource());
        lead.setContactName(request.getContactName());
        lead.setContactEmail(request.getContactEmail());
        lead.setContactPhone(request.getContactPhone());
        lead.setCompany(request.getCompany());
        lead.setNotes(request.getNotes());
        lead.setStatus("new");
        lead.setScore(0);
        Lead saved = leadRepository.save(lead);
        auditService.record(principal, "lead.create", "lead", saved.getId().toString(),
                Map.of("source", saved.getSource(), "contact", saved.getContactName()));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lead> get(@AuthenticationPrincipal AgentosUserPrincipal principal, @PathVariable UUID id) {
        return leadRepository.findById(id)
                .filter(l -> l.getTenantId().equals(principal.tenantId()))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AgentosUserPrincipal principal, @PathVariable UUID id) {
        return leadRepository.findById(id)
                .filter(l -> l.getTenantId().equals(principal.tenantId()))
                .map(l -> {
                    leadRepository.delete(l);
                    auditService.record(principal, "lead.delete", "lead", l.getId().toString(), Map.of());
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
