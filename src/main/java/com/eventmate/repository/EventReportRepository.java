package com.eventmate.repository;

import com.eventmate.model.EventReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EventReportRepository extends MongoRepository<EventReport, String> {
    List<EventReport> findByEventId(String eventId);
    boolean existsByEventIdAndUserId(String eventId, String userId);
    List<EventReport> findByOrderByCreatedAtDesc();
}
