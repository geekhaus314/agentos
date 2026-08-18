package com.agentos.core.dto;

import jakarta.validation.constraints.NotBlank;

public class LeadRequest {

    @NotBlank(message = "source must not be blank")
    private String source;
    @NotBlank(message = "contactName must not be blank")
    private String contactName;
    private String contactEmail;
    private String contactPhone;
    private String company;
    private String notes;

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
