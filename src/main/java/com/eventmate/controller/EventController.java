package com.eventmate.controller;

import com.eventmate.model.Event;
import com.eventmate.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    private final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(value = "organizerId", required = false) String organizerId) {
        List<Event> events;
        if (organizerId != null && !organizerId.isEmpty()) {
            events = eventRepository.findByOrganizerIdOrderByCreatedAtDesc(organizerId);
        } else {
            events = eventRepository.findAllByOrderByCreatedAtDesc();
        }
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<?> createEvent(
            @RequestParam("eventName") String eventName,
            @RequestParam("eventType") String eventType,
            @RequestParam("description") String description,
            @RequestParam("eventDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date eventDate,
            @RequestParam("eventTime") String eventTime,
            @RequestParam("location") String location,
            @RequestParam(value = "jobVacancy", required = false, defaultValue = "false") boolean jobVacancy,
            @RequestParam(value = "adsOpportunity", required = false, defaultValue = "false") boolean adsOpportunity,
            @RequestParam(value = "organizerId", required = false) String organizerId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            Event event = new Event();
            event.setOrganizerId(organizerId);
            event.setEventName(eventName);
            event.setEventType(eventType);
            event.setDescription(description);
            event.setEventDate(eventDate);
            event.setEventTime(eventTime);
            event.setLocation(location);
            event.setJobVacancy(jobVacancy);
            event.setAdsOpportunity(adsOpportunity);

            if (image != null && !image.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFileName = StringUtils.cleanPath(image.getOriginalFilename());
                String fileName = System.currentTimeMillis() + "_" + originalFileName;
                Path filePath = uploadPath.resolve(fileName);
                
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(fileName)
                        .toUriString();

                event.setImageUrl(fileDownloadUri);
            }

            Event savedEvent = eventRepository.save(event);
            return ResponseEntity.status(201).body(savedEvent);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("Could not store file.");
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable String id) {
        return eventRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(
            @PathVariable String id,
            @RequestParam("eventName") String eventName,
            @RequestParam("eventType") String eventType,
            @RequestParam("description") String description,
            @RequestParam("eventDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date eventDate,
            @RequestParam("eventTime") String eventTime,
            @RequestParam("location") String location,
            @RequestParam(value = "jobVacancy", required = false, defaultValue = "false") boolean jobVacancy,
            @RequestParam(value = "adsOpportunity", required = false, defaultValue = "false") boolean adsOpportunity,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return eventRepository.findById(id).map(event -> {
            try {
                event.setEventName(eventName);
                event.setEventType(eventType);
                event.setDescription(description);
                event.setEventDate(eventDate);
                event.setEventTime(eventTime);
                event.setLocation(location);
                event.setJobVacancy(jobVacancy);
                event.setAdsOpportunity(adsOpportunity);

                if (image != null && !image.isEmpty()) {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    String originalFileName = StringUtils.cleanPath(image.getOriginalFilename());
                    String fileName = System.currentTimeMillis() + "_" + originalFileName;
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/uploads/").path(fileName).toUriString();
                    event.setImageUrl(fileDownloadUri);
                }

                Event updatedEvent = eventRepository.save(event);
                return ResponseEntity.ok(updatedEvent);
            } catch (IOException ex) {
                return ResponseEntity.badRequest().body("Could not store file.");
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable String id) {
        if (!eventRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        eventRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/opportunities")
    public ResponseEntity<?> addOpportunities(@PathVariable String id, @RequestBody Map<String, String> payload) {
        return eventRepository.findById(id).map(event -> {
            
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
            return ResponseEntity.ok().body("Opportunities saved successfully");
        }).orElse(ResponseEntity.notFound().build());
    }
}
