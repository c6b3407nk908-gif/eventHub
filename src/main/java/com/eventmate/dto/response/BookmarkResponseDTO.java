package com.eventmate.dto.response;

import java.time.LocalDateTime;

public class BookmarkResponseDTO {
    
    private String eventId;
    private String title;
    private String location;
    private String imageUrl;
    private LocalDateTime savedAt;

    public BookmarkResponseDTO(String eventId, String title, String location, String imageUrl, LocalDateTime savedAt) {
        this.eventId = eventId;
        this.title = title;
        this.location = location;
        this.imageUrl = imageUrl;
        this.savedAt = savedAt;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}
