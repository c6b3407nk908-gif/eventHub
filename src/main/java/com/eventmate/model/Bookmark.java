package com.eventmate.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "bookmarks")
@CompoundIndex(def = "{'userId': 1, 'eventId': 1}", unique = true)
public class Bookmark {

    @Id
    private String id;

    private String userId;

    private String eventId;

    private LocalDateTime createdAt;

    public Bookmark() {
        this.createdAt = LocalDateTime.now();
    }

    public Bookmark(String userId, String eventId) {
        this.userId = userId;
        this.eventId = eventId;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
