package com.eventmate.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "applications")
public class Application {

    @Id
    private String id;
    private String eventId;
    private String eventName;
    private String organizerId;
    
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    
    // "JOB" or "SPONSOR"
    private String type;
    
    // The specific role or package they applied for
    private String roleOrPackageName;
    
    private Date createdAt;

    public Application() {
        this.createdAt = new Date();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    public String getApplicantEmail() { return applicantEmail; }
    public void setApplicantEmail(String applicantEmail) { this.applicantEmail = applicantEmail; }

    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRoleOrPackageName() { return roleOrPackageName; }
    public void setRoleOrPackageName(String roleOrPackageName) { this.roleOrPackageName = roleOrPackageName; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
