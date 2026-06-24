package com.eventmate.dto.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class EventFilterRequestDTO {
    
    // Pagination and Sorting
    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String direction = "desc";

    // Filters
    private String category;
    private String type;
    private String location;
    private String organizerId;
    private String search;
    private String keyword; // For backwards compatibility with /search endpoint
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date eventDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date endDate;
    
    private Boolean upcoming;

    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOrganizerId() { return organizerId; }
    public void setOrganizerId(String organizerId) { this.organizerId = organizerId; }

    public String getSearch() { return search != null ? search : keyword; }
    public void setSearch(String search) { this.search = search; }
    
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Boolean getUpcoming() { return upcoming; }
    public void setUpcoming(Boolean upcoming) { this.upcoming = upcoming; }
}
