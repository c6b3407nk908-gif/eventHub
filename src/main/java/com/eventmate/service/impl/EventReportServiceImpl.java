package com.eventmate.service.impl;

import com.eventmate.exception.BadRequestException;
import com.eventmate.model.Event;
import com.eventmate.model.EventReport;
import com.eventmate.repository.EventRepository;
import com.eventmate.repository.EventReportRepository;
import com.eventmate.service.EventReportService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import com.eventmate.service.ImageStorageService;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class EventReportServiceImpl implements EventReportService {

    private final EventReportRepository eventReportRepository;
    private final EventRepository eventRepository;
    private final ImageStorageService imageStorageService;

    public EventReportServiceImpl(EventReportRepository eventReportRepository, EventRepository eventRepository, ImageStorageService imageStorageService) {
        this.eventReportRepository = eventReportRepository;
        this.eventRepository = eventRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public EventReport createReport(EventReport report, MultipartFile screenshot) {
        if (report.getEventId() == null || report.getEventId().isEmpty()) {
            throw new BadRequestException("Event ID is required");
        }
        
        Optional<Event> eventOpt = eventRepository.findById(report.getEventId());
        if (eventOpt.isEmpty()) {
            throw new BadRequestException("Event not found");
        }
        Event event = eventOpt.get();
        
        if (event.getOrganizerId() != null && event.getOrganizerId().equals(report.getUserId())) {
            throw new BadRequestException("Host cannot report their own event");
        }
        
        if (eventReportRepository.existsByEventIdAndUserId(report.getEventId(), report.getUserId())) {
            throw new BadRequestException("You have already reported this event");
        }
        
        report.setEventName(event.getEventName());
        
        if (screenshot != null && !screenshot.isEmpty()) {
            try {
                String url = imageStorageService.upload(screenshot);
                report.setScreenshotUrl(url);
            } catch (IOException e) {
                throw new BadRequestException("Failed to upload screenshot");
            }
        }
        
        return eventReportRepository.save(report);
    }

    @Override
    public boolean hasUserReportedEvent(String eventId, String userId) {
        if (eventId == null || userId == null) return false;
        return eventReportRepository.existsByEventIdAndUserId(eventId, userId);
    }

    @Override
    public List<EventReport> getAllReports() {
        return eventReportRepository.findByOrderByCreatedAtDesc();
    }
}
