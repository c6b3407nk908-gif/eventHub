package com.eventmate.controller;

import com.eventmate.model.Feedback;
import com.eventmate.model.Event;
import com.eventmate.repository.FeedbackRepository;
import com.eventmate.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    @PostMapping
    public ResponseEntity<?> createFeedback(@RequestBody Feedback feedback) {
        if (feedback.getEventId() == null || feedback.getEventId().isEmpty()) {
            return ResponseEntity.badRequest().body("Event ID is required");
        }
        
        if ("website".equals(feedback.getEventId())) {
            feedback.setEventName("Website Feedback");
        } else {
            Optional<Event> eventOpt = eventRepository.findById(feedback.getEventId());
            if (eventOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Event not found");
            }
            
            Event event = eventOpt.get();
            if (!"completed".equals(event.getStatus())) {
                return ResponseEntity.badRequest().body("Reviews and ratings can only be given for completed events.");
            }
            feedback.setEventName(event.getEventName());
        }
        
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5");
        }
        Feedback savedFeedback = feedbackRepository.save(feedback);
        return ResponseEntity.status(201).body(savedFeedback);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackRepository.findByOrderByCreatedAtDesc());
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Feedback>> getFeedbacksByEvent(@PathVariable String eventId) {
        return ResponseEntity.ok(feedbackRepository.findByEventId(eventId));
    }
}
