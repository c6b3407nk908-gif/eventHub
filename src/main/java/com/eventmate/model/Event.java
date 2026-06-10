package com.eventmate.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "events")
public class Event {

    @Id
    private String id;
    private String organizerId;
    private String eventName;
    private String eventType;
    private String description;
    private Date eventDate;
    private String eventTime;
    private String location;
    private String imageUrl;
    private boolean jobVacancy;
    private boolean adsOpportunity;
    private boolean verified = false;
    private List<JobOpportunity> jobs = new ArrayList<>();
    private List<SponsorshipPackage> sponsorships = new ArrayList<>();
    private Date createdAt;

    public Event() {
        this.createdAt = new Date();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public String getEventTime() { return eventTime; }
    public void setEventTime(String eventTime) { this.eventTime = eventTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isJobVacancy() { return jobVacancy; }
    public void setJobVacancy(boolean jobVacancy) { this.jobVacancy = jobVacancy; }

    public boolean isAdsOpportunity() { return adsOpportunity; }
    public void setAdsOpportunity(boolean adsOpportunity) { this.adsOpportunity = adsOpportunity; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }

    public List<JobOpportunity> getJobs() { return jobs; }
    public void setJobs(List<JobOpportunity> jobs) { this.jobs = jobs; }

    public List<SponsorshipPackage> getSponsorships() { return sponsorships; }
    public void setSponsorships(List<SponsorshipPackage> sponsorships) { this.sponsorships = sponsorships; }

    public String getStatus() {
        if (this.eventDate == null) return "coming";
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(this.eventDate);
        
        String timeStr = this.eventTime != null && !this.eventTime.isEmpty() ? this.eventTime : "00:00";
        
        try {
            java.text.SimpleDateFormat fullSdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date eventStart = fullSdf.parse(dateStr + "T" + timeStr);
            
            long eventEndMillis = eventStart.getTime() + (3L * 60 * 60 * 1000); // assume 3 hours duration
            long nowMillis = System.currentTimeMillis();
            
            if (nowMillis < eventStart.getTime()) {
                return "coming";
            } else if (nowMillis >= eventStart.getTime() && nowMillis <= eventEndMillis) {
                return "ongoing";
            } else {
                return "completed";
            }
        } catch (Exception e) {
            Date now = new Date();
            if (now.before(this.eventDate)) {
                return "coming";
            } else {
                return "completed";
            }
        }
    }

    public static class JobOpportunity {
        private String roleTitle;
        private String jobType;
        private String compensation;
        private String roleDescription;

        public JobOpportunity() {}

        public String getRoleTitle() { return roleTitle; }
        public void setRoleTitle(String roleTitle) { this.roleTitle = roleTitle; }

        public String getJobType() { return jobType; }
        public void setJobType(String jobType) { this.jobType = jobType; }

        public String getCompensation() { return compensation; }
        public void setCompensation(String compensation) { this.compensation = compensation; }

        public String getRoleDescription() { return roleDescription; }
        public void setRoleDescription(String roleDescription) { this.roleDescription = roleDescription; }
    }

    public static class SponsorshipPackage {
        private String packageName;
        private String packagePrice;
        private String packagePerks;

        public SponsorshipPackage() {}

        public String getPackageName() { return packageName; }
        public void setPackageName(String packageName) { this.packageName = packageName; }

        public String getPackagePrice() { return packagePrice; }
        public void setPackagePrice(String packagePrice) { this.packagePrice = packagePrice; }

        public String getPackagePerks() { return packagePerks; }
        public void setPackagePerks(String packagePerks) { this.packagePerks = packagePerks; }
    }
}
