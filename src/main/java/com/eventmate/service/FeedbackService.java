package com.eventmate.service;

import com.eventmate.model.Feedback;
import java.util.List;

public interface FeedbackService {
    Feedback createFeedback(Feedback feedback);
    List<Feedback> getAllFeedbacks();
    List<Feedback> getFeedbacksByEvent(String eventId);
}
