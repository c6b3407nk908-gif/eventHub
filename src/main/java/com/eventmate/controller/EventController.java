package com.eventmate.controller;

import com.eventmate.model.Event;
import com.eventmate.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eventmate.dto.response.EventResponseDTO;
import com.eventmate.dto.response.PaginatedResponseDTO;
import com.eventmate.dto.request.EventFilterRequestDTO;

import org.springframework.format.annotation.DateTimeFormat;
import com.eventmate.model.EventCategory;
import java.util.Arrays;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        List<String> categories = Arrays.stream(EventCategory.values())
                .map(Enum::name)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<EventResponseDTO>> getEvents(@ModelAttribute EventFilterRequestDTO request) {
        request.setIsSearchRoute(false);
        PaginatedResponseDTO<EventResponseDTO> events = eventService.filterEvents(request);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<PaginatedResponseDTO<EventResponseDTO>> searchEvents(@ModelAttribute EventFilterRequestDTO request) {
        request.setIsSearchRoute(true);
        PaginatedResponseDTO<EventResponseDTO> events = eventService.filterEvents(request);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("eventType") String eventType,
            @RequestParam("description") String description,
            @RequestParam("eventDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date eventDate,
            @RequestParam("eventTime") String eventTime,
            @RequestParam("location") String location,
            @RequestParam(value = "jobVacancy", required = false, defaultValue = "false") boolean jobVacancy,
            @RequestParam(value = "adsOpportunity", required = false, defaultValue = "false") boolean adsOpportunity,
            @RequestParam(value = "opportunitiesDeadline", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date opportunitiesDeadline,
            @RequestParam(value = "organizerId", required = false) String organizerId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Event savedEvent = eventService.createEvent(eventName, category, eventType, description, eventDate, eventTime, location, jobVacancy, adsOpportunity, opportunitiesDeadline, organizerId, image);
        return ResponseEntity.status(201).body(savedEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        Event event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable String id,
            @RequestParam("eventName") String eventName,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam("eventType") String eventType,
            @RequestParam("description") String description,
            @RequestParam("eventDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date eventDate,
            @RequestParam("eventTime") String eventTime,
            @RequestParam("location") String location,
            @RequestParam(value = "jobVacancy", required = false, defaultValue = "false") boolean jobVacancy,
            @RequestParam(value = "adsOpportunity", required = false, defaultValue = "false") boolean adsOpportunity,
            @RequestParam(value = "opportunitiesDeadline", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date opportunitiesDeadline,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        Event updatedEvent = eventService.updateEvent(id, eventName, category, eventType, description, eventDate, eventTime, location, jobVacancy, adsOpportunity, opportunitiesDeadline, image);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/verify")
    public ResponseEntity<?> verifyEvent(@PathVariable String id, @RequestParam("status") String status) {
        Event updatedEvent = eventService.verifyEvent(id, status);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{id}/apply-verification")
    public ResponseEntity<?> applyVerification(
            @PathVariable String id,
            @RequestParam("certificate") MultipartFile certificate,
            @AuthenticationPrincipal String userId) {
        Event updatedEvent = eventService.applyVerification(id, certificate, userId);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{id}/opportunities")
    public ResponseEntity<?> addOpportunities(@PathVariable String id, @RequestBody Map<String, String> payload) {
        eventService.addOpportunities(id, payload);
        return ResponseEntity.ok().body("Opportunities saved successfully");
    }

    @PutMapping("/{id}/interest")
    public ResponseEntity<?> toggleInterest(@PathVariable String id, @AuthenticationPrincipal String userId) {
        Event updatedEvent = eventService.toggleInterest(id, userId);
        return ResponseEntity.ok(new EventResponseDTO(updatedEvent));
    }
}
