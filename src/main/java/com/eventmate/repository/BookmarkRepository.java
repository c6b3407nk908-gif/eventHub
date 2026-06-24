package com.eventmate.repository;

import com.eventmate.model.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends MongoRepository<Bookmark, String> {
    
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
    
    Optional<Bookmark> findByUserIdAndEventId(String userId, String eventId);
    
    void deleteByUserIdAndEventId(String userId, String eventId);
    
    long countByEventId(String eventId);
    
    boolean existsByUserIdAndEventId(String userId, String eventId);
}
