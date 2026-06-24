package com.eventmate.service.impl;

import com.eventmate.exception.BadRequestException;
import com.eventmate.model.ApplicantReport;
import com.eventmate.repository.ApplicantReportRepository;
import com.eventmate.service.ApplicantReportService;
import com.eventmate.service.ImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
public class ApplicantReportServiceImpl implements ApplicantReportService {

    private final ApplicantReportRepository applicantReportRepository;
    private final ImageStorageService imageStorageService;

    public ApplicantReportServiceImpl(ApplicantReportRepository applicantReportRepository, ImageStorageService imageStorageService) {
        this.applicantReportRepository = applicantReportRepository;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public ApplicantReport createReport(ApplicantReport report, MultipartFile screenshot) {
        if (report.getApplicationId() == null || report.getApplicationId().isEmpty()) {
            throw new BadRequestException("Application ID is required");
        }
        
        if (applicantReportRepository.existsByApplicationIdAndOrganizerId(report.getApplicationId(), report.getOrganizerId())) {
            throw new BadRequestException("You have already reported this applicant");
        }
        
        if (screenshot != null && !screenshot.isEmpty()) {
            try {
                String url = imageStorageService.upload(screenshot);
                report.setScreenshotUrl(url);
            } catch (IOException e) {
                throw new BadRequestException("Failed to upload screenshot");
            }
        }
        
        return applicantReportRepository.save(report);
    }

    @Override
    public boolean hasOrganizerReportedApplicant(String applicationId, String organizerId) {
        if (applicationId == null || organizerId == null) return false;
        return applicantReportRepository.existsByApplicationIdAndOrganizerId(applicationId, organizerId);
    }

    @Override
    public List<ApplicantReport> getReportsByApplication(String applicationId) {
        return applicantReportRepository.findByApplicationId(applicationId);
    }

    @Override
    public List<ApplicantReport> getAllReports() {
        return applicantReportRepository.findByOrderByCreatedAtDesc();
    }
}
