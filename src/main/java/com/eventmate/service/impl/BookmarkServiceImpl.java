package com.eventmate.service.impl;

import com.eventmate.dto.response.BookmarkResponseDTO;
import com.eventmate.dto.response.PaginatedResponseDTO;
import com.eventmate.exception.BadRequestException;
import com.eventmate.exception.ResourceNotFoundException;
import com.eventmate.model.Bookmark;
import com.eventmate.model.Event;
import com.eventmate.repository.BookmarkRepository;
import com.eventmate.repository.EventRepository;
import com.eventmate.service.BookmarkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final EventRepository eventRepository;

    public BookmarkServiceImpl(BookmarkRepository bookmarkRepository, EventRepository eventRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public void saveBookmark(String userId, String eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }
        
        if (bookmarkRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new BadRequestException("Event is already bookmarked");
        }
        
        Bookmark bookmark = new Bookmark(userId, eventId);
        bookmarkRepository.save(bookmark);
    }

    @Override
    public void removeBookmark(String userId, String eventId) {
        bookmarkRepository.deleteByUserIdAndEventId(userId, eventId);
    }

    @Override
    public PaginatedResponseDTO<BookmarkResponseDTO> getUserBookmarks(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        List<BookmarkResponseDTO> dtoList = bookmarkPage.getContent().stream().map(bookmark -> {
            Event event = eventRepository.findById(bookmark.getEventId()).orElse(null);
            if (event != null) {
                return new BookmarkResponseDTO(
                    event.getId(),
                    event.getEventName(),
                    event.getLocation(),
                    event.getImageUrl(),
                    bookmark.getCreatedAt()
                );
            }
            return null;
        }).filter(dto -> dto != null).collect(Collectors.toList());

        return new PaginatedResponseDTO<>(
                dtoList,
                bookmarkPage.getNumber(),
                bookmarkPage.getSize(),
                bookmarkPage.getTotalElements(),
                bookmarkPage.getTotalPages(),
                bookmarkPage.isLast()
        );
    }

    @Override
    public boolean isBookmarked(String userId, String eventId) {
        return bookmarkRepository.existsByUserIdAndEventId(userId, eventId);
    }

    @Override
    public long getBookmarkCount(String eventId) {
        return bookmarkRepository.countByEventId(eventId);
    }
}
