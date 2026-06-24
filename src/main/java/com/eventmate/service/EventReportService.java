package com.eventmate.service;

import com.eventmate.model.EventReport;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface EventReportService {
    EventReport createReport(EventReport report, MultipartFile screenshot);
    boolean hasUserReportedEvent(String eventId, String userId);
    List<EventReport> getAllReports();
}
