package com.eventmate.controller;

import com.eventmate.model.Application;
import com.eventmate.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @PostMapping
    public ResponseEntity<?> submitApplication(@RequestBody Application application) {
        Application saved = applicationRepository.save(application);
        return ResponseEntity.status(201).body(saved);
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<Application>> getApplicationsForOrganizer(@PathVariable String organizerId) {
        List<Application> applications = applicationRepository.findByOrganizerIdOrderByCreatedAtDesc(organizerId);
        return ResponseEntity.ok(applications);
    }
}
