package com.eventmate.controller;

import com.eventmate.dto.response.BookmarkResponseDTO;
import com.eventmate.dto.response.PaginatedResponseDTO;
import com.eventmate.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<?> saveBookmark(@PathVariable String eventId, @AuthenticationPrincipal String userId) {
        bookmarkService.saveBookmark(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> removeBookmark(@PathVariable String eventId, @AuthenticationPrincipal String userId) {
        bookmarkService.removeBookmark(userId, eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<BookmarkResponseDTO>> getUserBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal String userId) {
        
        PaginatedResponseDTO<BookmarkResponseDTO> bookmarks = bookmarkService.getUserBookmarks(userId, page, size);
        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/status/{eventId}")
    public ResponseEntity<?> checkBookmarkStatus(@PathVariable String eventId, @AuthenticationPrincipal String userId) {
        boolean isBookmarked = bookmarkService.isBookmarked(userId, eventId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isBookmarked", isBookmarked);
        return ResponseEntity.ok(response);
    }
}
