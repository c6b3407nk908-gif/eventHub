package com.eventmate.repository;

import com.eventmate.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByEventId(String eventId);
    List<Feedback> findByOrderByCreatedAtDesc();
}
