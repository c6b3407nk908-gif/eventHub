package com.eventmate.repository;

import com.eventmate.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;

import com.eventmate.dto.request.EventFilterRequestDTO;

public interface EventRepositoryCustom {
    Page<Event> getFilteredEvents(EventFilterRequestDTO request, Pageable pageable);
}
