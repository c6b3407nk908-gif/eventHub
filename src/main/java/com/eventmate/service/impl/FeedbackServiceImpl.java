package com.eventmate.service.impl;

import com.eventmate.exception.BadRequestException;
import com.eventmate.model.Event;
import com.eventmate.model.Feedback;
import com.eventmate.repository.EventRepository;
import com.eventmate.repository.FeedbackRepository;
import com.eventmate.service.FeedbackService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, EventRepository eventRepository) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public Feedback createFeedback(Feedback feedback) {
        if (feedback.getEventId() == null || feedback.getEventId().isEmpty()) {
            throw new BadRequestException("Event ID is required");
        }
        
        if ("website".equals(feedback.getEventId())) {
            feedback.setEventName("Website Feedback");
        } else {
            Optional<Event> eventOpt = eventRepository.findById(feedback.getEventId());
            if (eventOpt.isEmpty()) {
                throw new BadRequestException("Event not found");
            }
            
            Event event = eventOpt.get();
            if (!"completed".equals(event.getStatus())) {
                throw new BadRequestException("Reviews and ratings can only be given for completed events.");
            }
            feedback.setEventName(event.getEventName());
        }
        
        if (feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }
        return feedbackRepository.save(feedback);
    }

    @Override
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findByOrderByCreatedAtDesc();
    }

    @Override
    public List<Feedback> getFeedbacksByEvent(String eventId) {
        return feedbackRepository.findByEventId(eventId);
    }
}
