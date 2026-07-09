package com.eventmate.service.impl;

import com.eventmate.exception.BadRequestException;
import com.eventmate.model.ApplicantReport;
import com.eventmate.model.Application;
import com.eventmate.repository.ApplicantReportRepository;
import com.eventmate.repository.ApplicationRepository;
import com.eventmate.service.ApplicantReportService;
import com.eventmate.service.ImageStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantReportServiceImpl implements ApplicantReportService {

    private final ApplicantReportRepository applicantReportRepository;
    private final ApplicationRepository applicationRepository;
    private final ImageStorageService imageStorageService;

    public ApplicantReportServiceImpl(ApplicantReportRepository applicantReportRepository, ApplicationRepository applicationRepository, ImageStorageService imageStorageService) {
        this.applicantReportRepository = applicantReportRepository;
        this.applicationRepository = applicationRepository;
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

        Optional<Application> applicationOpt = applicationRepository.findById(report.getApplicationId());
        if (applicationOpt.isPresent()) {
            Application application = applicationOpt.get();
            report.setApplicantEmail(application.getApplicantEmail());
            report.setEventId(application.getEventId());
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
