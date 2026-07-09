package com.eventmate.repository;

import com.eventmate.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import com.eventmate.dto.request.EventFilterRequestDTO;
import java.util.Calendar;

@Repository
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public EventRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<Event> getFilteredEvents(EventFilterRequestDTO request, Pageable pageable) {
        Query query = new Query();

        if (request.getCategory() != null && !request.getCategory().trim().isEmpty()) {
            query.addCriteria(Criteria.where("category").is(request.getCategory().trim().toUpperCase()));
        }

        if (request.getType() != null && !request.getType().trim().isEmpty()) {
            query.addCriteria(Criteria.where("eventType").regex(request.getType().trim(), "i"));
        }

        if (request.getLocation() != null && !request.getLocation().trim().isEmpty()) {
            query.addCriteria(Criteria.where("location").regex(request.getLocation().trim(), "i"));
        }

        if (request.getOrganizerId() != null && !request.getOrganizerId().trim().isEmpty()) {
            query.addCriteria(Criteria.where("organizerId").is(request.getOrganizerId().trim()));
        }

        if (request.getSearch() != null && !request.getSearch().trim().isEmpty()) {
            String searchPattern = ".*" + request.getSearch().trim() + ".*";
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("eventName").regex(searchPattern, "i"),
                    Criteria.where("description").regex(searchPattern, "i"),
                    Criteria.where("location").regex(searchPattern, "i"),
                    Criteria.where("eventType").regex(searchPattern, "i")
            );
            query.addCriteria(searchCriteria);
        }

        if (request.getEventDate() != null) {
            // Match exact day by creating a range from start of day to end of day
            Calendar cal = Calendar.getInstance();
            cal.setTime(request.getEventDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date startOfDay = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date startOfNextDay = cal.getTime();
            
            query.addCriteria(Criteria.where("eventDate").gte(startOfDay).lt(startOfNextDay));
        } else if (request.getStartDate() != null || request.getEndDate() != null) {
            Criteria dateCriteria = Criteria.where("eventDate");
            if (request.getStartDate() != null) {
                dateCriteria.gte(request.getStartDate());
            }
            if (request.getEndDate() != null) {
                dateCriteria.lte(request.getEndDate());
            }
            query.addCriteria(dateCriteria);
        }

        if (Boolean.TRUE.equals(request.getUpcoming())) {
            query.addCriteria(Criteria.where("eventDate").gte(new Date()));
        } else if (Boolean.TRUE.equals(request.getIsSearchRoute()) && (request.getOrganizerId() == null || request.getOrganizerId().trim().isEmpty())) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date limitDate = cal.getTime();
            query.addCriteria(Criteria.where("eventDate").gte(limitDate));
        }

        long total = mongoTemplate.count(query, Event.class);
        
        query.with(pageable);
        List<Event> events = mongoTemplate.find(query, Event.class);

        return new PageImpl<>(events, pageable, total);
    }
}
