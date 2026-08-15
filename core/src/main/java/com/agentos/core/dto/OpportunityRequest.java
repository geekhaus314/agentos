package com.agentos.core.dto;

import java.math.BigDecimal;

public class OpportunityRequest {

    private String source;
    private String priority;
    private BigDecimal estimatedValue;
    private String notes;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public BigDecimal getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(BigDecimal estimatedValue) { this.estimatedValue = estimatedValue; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
