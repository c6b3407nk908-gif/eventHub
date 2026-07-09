package com.eventmate.controller;

import com.eventmate.model.Application;
import com.eventmate.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<?> submitApplication(@RequestBody Application application) {
        Application saved = applicationService.submitApplication(application);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Application>> getApplicationsForOrganizer(@PathVariable String organizerId) {
        List<Application> applications = applicationService.getApplicationsForOrganizer(organizerId);
        return ResponseEntity.ok(applications);
    }
}
