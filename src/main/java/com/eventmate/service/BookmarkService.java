package com.eventmate.service;

import com.eventmate.dto.response.BookmarkResponseDTO;
import com.eventmate.dto.response.PaginatedResponseDTO;

public interface BookmarkService {
    void saveBookmark(String userId, String eventId);
    void removeBookmark(String userId, String eventId);
    PaginatedResponseDTO<BookmarkResponseDTO> getUserBookmarks(String userId, int page, int size);
    boolean isBookmarked(String userId, String eventId);
    long getBookmarkCount(String eventId);
}
