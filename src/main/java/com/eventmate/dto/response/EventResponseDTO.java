package com.eventmate.dto.response;

import com.eventmate.model.Event;
import com.eventmate.model.EventCategory;
import java.util.Date;
import java.util.List;

public class EventResponseDTO {

    private String id;
    private String organizerId;
    private String eventName;
    private String eventType;
    private String description;
    private EventCategory category;
    private Date eventDate;
    private String eventTime;
    private String location;
    private String imageUrl;
    private boolean jobVacancy;
    private boolean adsOpportunity;
    private Date opportunitiesDeadline;
    private boolean verified;
    private List<Event.JobOpportunity> jobs;
    private List<Event.SponsorshipPackage> sponsorships;
    private int interestCount;
    private Date createdAt;
    private String status;
    private String verificationCertificateUrl;
    private String verificationStatus;

    public EventResponseDTO() {}

    public EventResponseDTO(Event event) {
        this.id = event.getId();
        this.organizerId = event.getOrganizerId();
        this.eventName = event.getEventName();
        this.eventType = event.getEventType();
        this.description = event.getDescription();
        this.category = event.getCategory();
        this.eventDate = event.getEventDate();
        this.eventTime = event.getEventTime();
        this.location = event.getLocation();
        this.imageUrl = event.getImageUrl();
        this.jobVacancy = event.isJobVacancy();
        this.adsOpportunity = event.isAdsOpportunity();
        this.opportunitiesDeadline = event.getOpportunitiesDeadline();
        this.verified = event.isVerified();
        this.jobs = event.getJobs();
        this.sponsorships = event.getSponsorships();
        this.interestCount = event.getInterestCount();
        this.createdAt = event.getCreatedAt();
        this.status = event.getStatus();
        this.verificationCertificateUrl = event.getVerificationCertificateUrl();
        this.verificationStatus = event.getVerificationStatus();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public EventCategory getCategory() { return category; }
    public void setCategory(EventCategory category) { this.category = category; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isJobVacancy() { return jobVacancy; }
    public void setJobVacancy(boolean jobVacancy) { this.jobVacancy = jobVacancy; }

    public boolean isAdsOpportunity() { return adsOpportunity; }
    public void setAdsOpportunity(boolean adsOpportunity) { this.adsOpportunity = adsOpportunity; }

    public Date getOpportunitiesDeadline() { return opportunitiesDeadline; }
    public void setOpportunitiesDeadline(Date opportunitiesDeadline) { this.opportunitiesDeadline = opportunitiesDeadline; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public List<Event.JobOpportunity> getJobs() { return jobs; }
    public void setJobs(List<Event.JobOpportunity> jobs) { this.jobs = jobs; }

    public List<Event.SponsorshipPackage> getSponsorships() { return sponsorships; }
    public void setSponsorships(List<Event.SponsorshipPackage> sponsorships) { this.sponsorships = sponsorships; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public int getInterestCount() { return interestCount; }
    public void setInterestCount(int interestCount) { this.interestCount = interestCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVerificationCertificateUrl() { return verificationCertificateUrl; }
    public void setVerificationCertificateUrl(String verificationCertificateUrl) { this.verificationCertificateUrl = verificationCertificateUrl; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
}
