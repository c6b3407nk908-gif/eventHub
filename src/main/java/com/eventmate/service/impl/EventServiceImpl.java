package com.eventmate.service.impl;

import java.util.Arrays;
import com.eventmate.exception.BadRequestException;
import com.eventmate.exception.ResourceNotFoundException;
import com.eventmate.model.Event;
import com.eventmate.model.EventCategory;
import com.eventmate.repository.EventRepository;
import com.eventmate.service.EventService;
import com.eventmate.service.ImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.eventmate.dto.response.EventResponseDTO;
import com.eventmate.dto.response.PaginatedResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;
import com.eventmate.dto.request.EventFilterRequestDTO;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;

    public EventServiceImpl(EventRepository eventRepository, ImageStorageService imageStorageService) {
        this.eventRepository = eventRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public PaginatedResponseDTO<EventResponseDTO> getEvents(int page, int size, String sortBy, String direction, String category, String type, String location, String organizerId, String search, Date startDate, Date endDate, Boolean upcoming) {
        EventFilterRequestDTO request = new EventFilterRequestDTO();
        request.setPage(page);
        request.setSize(size);
        request.setSortBy(sortBy);
        request.setDirection(direction);
        request.setCategory(category);
        request.setType(type);
        request.setLocation(location);
        request.setOrganizerId(organizerId);
        request.setSearch(search);
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        request.setUpcoming(upcoming);
        return filterEvents(request);
    }

    @Override
    public PaginatedResponseDTO<EventResponseDTO> filterEvents(EventFilterRequestDTO request) {
        if (request.getSize() > 50) {
            request.setSize(50); // Cap size to 50 for performance
        }
        
        // Whitelist sort fields to prevent errors
        List<String> allowedSortFields = Arrays.asList("createdAt", "eventDate", "title", "eventName", "location", "interestCount");
        if (!allowedSortFields.contains(request.getSortBy())) {
            request.setSortBy("createdAt");
        }
        if ("title".equals(request.getSortBy())) {
            request.setSortBy("eventName");
        }

        Sort.Direction sortDir = "asc".equalsIgnoreCase(request.getDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(sortDir, request.getSortBy()));

        Page<Event> eventPage = eventRepository.getFilteredEvents(request, pageable);
        
        List<EventResponseDTO> dtoList = eventPage.getContent().stream()
                .map(EventResponseDTO::new)
                .collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                dtoList,
                eventPage.getNumber(),
                eventPage.getSize(),
                eventPage.getTotalElements(),
                eventPage.getTotalPages(),
                eventPage.isLast()
        );
    }

    @Override
    public Event createEvent(String eventName, String category, String eventType, String description, Date eventDate, String eventTime, String location, boolean jobVacancy, boolean adsOpportunity, Date opportunitiesDeadline, String organizerId, MultipartFile image) {
        try {
            Event event = new Event();
            event.setOrganizerId(organizerId);
            event.setEventName(eventName);
            if (category != null && !category.isEmpty()) {
                try {
                    event.setCategory(EventCategory.valueOf(category.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    event.setCategory(EventCategory.OTHER);
                }
            } else {
                event.setCategory(EventCategory.OTHER);
            }
            event.setEventType(eventType);
            event.setDescription(description);
            event.setEventDate(eventDate);
            event.setEventTime(eventTime);
            event.setLocation(location);
            event.setJobVacancy(jobVacancy);
            event.setAdsOpportunity(adsOpportunity);
            event.setOpportunitiesDeadline(opportunitiesDeadline);

            if (image != null && !image.isEmpty()) {
                event.setImageUrl(imageStorageService.upload(image));
            }

            return eventRepository.save(event);
        } catch (IOException ex) {
            throw new BadRequestException("Could not store image to cloud.");
        }
    }

    @Override
    public Event getEventById(String id) {
        return eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Override
    public Event updateEvent(String id, String eventName, String category, String eventType, String description, Date eventDate, String eventTime, String location, boolean jobVacancy, boolean adsOpportunity, Date opportunitiesDeadline, MultipartFile image) {
        Event event = getEventById(id);
        
        try {
            event.setEventName(eventName);
            if (category != null && !category.isEmpty()) {
                try {
                    event.setCategory(EventCategory.valueOf(category.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    event.setCategory(EventCategory.OTHER);
                }
            } else {
                event.setCategory(EventCategory.OTHER);
            }
            event.setEventType(eventType);
            event.setDescription(description);
            event.setEventDate(eventDate);
            event.setEventTime(eventTime);
            event.setLocation(location);
            event.setJobVacancy(jobVacancy);
            event.setAdsOpportunity(adsOpportunity);
            event.setOpportunitiesDeadline(opportunitiesDeadline);

            if (image != null && !image.isEmpty()) {
                String oldUrl = event.getImageUrl();
                event.setImageUrl(imageStorageService.upload(image));
                if (oldUrl != null && oldUrl.startsWith("http")) {
                    try {
                        imageStorageService.delete(oldUrl);
                    } catch (Exception ignored) {}
                }
            }

            return eventRepository.save(event);
        } catch (IOException ex) {
            throw new BadRequestException("Could not store image to cloud.");
        }
    }

    @Override
    public void deleteEvent(String id) {
        Event event = getEventById(id);
        if (event.getImageUrl() != null && event.getImageUrl().startsWith("http")) {
            try {
                imageStorageService.delete(event.getImageUrl());
            } catch (Exception ignored) {}
        }
        eventRepository.deleteById(id);
    }

    @Override
    public Event verifyEvent(String id, String status) {
        Event event = getEventById(id);
        if ("APPROVED".equalsIgnoreCase(status)) {
            event.setVerified(true);
            event.setVerificationStatus("APPROVED");
        } else if ("REJECTED".equalsIgnoreCase(status)) {
            event.setVerified(false);
            event.setVerificationStatus("REJECTED");
        } else {
            throw new BadRequestException("Invalid verification status: " + status);
        }
        return eventRepository.save(event);
    }

    @Override
    public Event applyVerification(String id, MultipartFile certificate, String userId) {
        Event event = getEventById(id);
        if (event.getOrganizerId() == null || !event.getOrganizerId().equals(userId)) {
            throw new BadRequestException("Only the event organizer can apply for verification.");
        }
        if (certificate == null || certificate.isEmpty()) {
            throw new BadRequestException("Certificate file is required.");
        }
        try {
            String certificateUrl = imageStorageService.upload(certificate);
            event.setVerificationCertificateUrl(certificateUrl);
            event.setVerificationStatus("PENDING");
            event.setVerified(false);
            return eventRepository.save(event);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload verification certificate.");
        }
    }

    @Override
    public void addOpportunities(String id, Map<String, String> payload) {
        Event event = getEventById(id);

        if (payload.containsKey("roleTitle") && payload.get("roleTitle") != null && !payload.get("roleTitle").isEmpty()) {
            Event.JobOpportunity job = new Event.JobOpportunity();
            job.setRoleTitle(payload.get("roleTitle"));
            job.setJobType(payload.get("jobType"));
            job.setCompensation(payload.get("compensation"));
            job.setRoleDescription(payload.get("roleDescription"));
            event.getJobs().add(job);
        }

        if (payload.containsKey("packageName") && payload.get("packageName") != null && !payload.get("packageName").isEmpty()) {
            Event.SponsorshipPackage sponsor = new Event.SponsorshipPackage();
            sponsor.setPackageName(payload.get("packageName"));
            sponsor.setPackagePrice(payload.get("packagePrice"));
            sponsor.setPackagePerks(payload.get("packagePerks"));
            event.getSponsorships().add(sponsor);
        }

        eventRepository.save(event);
    }

    @Override
    public Event toggleInterest(String eventId, String userId) {
        Event event = getEventById(eventId);
        
        List<String> interestedUsers = event.getInterestedUserIds();
        if (interestedUsers.contains(userId)) {
            interestedUsers.remove(userId);
            event.setInterestCount(event.getInterestCount() - 1);
        } else {
            interestedUsers.add(userId);
            event.setInterestCount(event.getInterestCount() + 1);
        }
        
        return eventRepository.save(event);
    }
}
