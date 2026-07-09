package com.eventmate.repository;

import com.eventmate.model.Application;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ApplicationRepository extends MongoRepository<Application, String> {
    List<Application> findByOrganizerIdOrderByCreatedAtDesc(String organizerId);
}
