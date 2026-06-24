package com.eventmate.controller;

import com.eventmate.model.EventReport;
import com.eventmate.model.User;
import com.eventmate.service.EventReportService;
import com.eventmate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class EventReportController {

    private final EventReportService eventReportService;
    private final UserService userService;

    public EventReportController(EventReportService eventReportService, UserService userService) {
        this.eventReportService = eventReportService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestParam("eventId") String eventId,
            @RequestParam("details") String details,
            @RequestParam(value = "screenshot", required = false) MultipartFile screenshot,
            @AuthenticationPrincipal String userId) {
            
        EventReport report = new EventReport();
        report.setEventId(eventId);
        report.setDetails(details);
        report.setUserId(userId);
        
        try {
            User user = userService.getUserProfile(userId);
            if (user != null) {
                report.setUserName(user.getName());
            }
        } catch (Exception ignored) {}
        
        EventReport savedReport = eventReportService.createReport(report, screenshot);
        return ResponseEntity.status(201).body(savedReport);
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<?> checkReportStatus(@PathVariable String eventId, @AuthenticationPrincipal String userId) {
        boolean hasReported = eventReportService.hasUserReportedEvent(eventId, userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReported", hasReported);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<EventReport>> getAllReports() {
        return ResponseEntity.ok(eventReportService.getAllReports());
    }
}
