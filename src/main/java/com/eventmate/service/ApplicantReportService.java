package com.eventmate.service;

import com.eventmate.model.ApplicantReport;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ApplicantReportService {
    ApplicantReport createReport(ApplicantReport report, MultipartFile screenshot);
    boolean hasOrganizerReportedApplicant(String applicationId, String organizerId);
    List<ApplicantReport> getReportsByApplication(String applicationId);
    List<ApplicantReport> getAllReports();
}
