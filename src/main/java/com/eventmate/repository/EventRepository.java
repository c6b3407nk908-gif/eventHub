package com.eventmate.repository;

import com.eventmate.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String>, EventRepositoryCustom {
    List<Event> findAllByOrderByCreatedAtDesc();
    List<Event> findByOrganizerIdOrderByCreatedAtDesc(String organizerId);
}
