package com.eventmate.service;

import com.eventmate.model.Event;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eventmate.dto.response.EventResponseDTO;
import com.eventmate.dto.response.PaginatedResponseDTO;

import com.eventmate.dto.request.EventFilterRequestDTO;

public interface EventService {
    PaginatedResponseDTO<EventResponseDTO> getEvents(int page, int size, String sortBy, String direction, String category, String type, String location, String organizerId, String search, Date startDate, Date endDate, Boolean upcoming);
    PaginatedResponseDTO<EventResponseDTO> filterEvents(EventFilterRequestDTO request);
    Event createEvent(String eventName, String category, String description, Date eventDate, String eventTime, String location, boolean jobVacancy, boolean adsOpportunity, Date opportunitiesDeadline, String organizerId, MultipartFile image);
    Event getEventById(String id);
    Event updateEvent(String id, String eventName, String category, String description, Date eventDate, String eventTime, String location, boolean jobVacancy, boolean adsOpportunity, Date opportunitiesDeadline, MultipartFile image);
    void deleteEvent(String id);
    Event verifyEvent(String id, String status);
    Event applyVerification(String id, MultipartFile certificate, String userId);
    void addOpportunities(String id, Map<String, String> payload);
    Event removeJobOpportunity(String eventId, String roleTitle);
    Event removeSponsorshipPackage(String eventId, String packageName);
    Event toggleInterest(String eventId, String userId);
}
