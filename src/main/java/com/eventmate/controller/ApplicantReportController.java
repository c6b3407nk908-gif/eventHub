package com.eventmate.controller;

import com.eventmate.model.ApplicantReport;
import com.eventmate.service.ApplicantReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applicant-reports")
public class ApplicantReportController {

    private final ApplicantReportService applicantReportService;

    public ApplicantReportController(ApplicantReportService applicantReportService) {
        this.applicantReportService = applicantReportService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestParam("applicationId") String applicationId,
            @RequestParam("details") String details,
            @RequestParam(value = "screenshot", required = false) MultipartFile screenshot,
            @AuthenticationPrincipal String organizerId) {
            
        ApplicantReport report = new ApplicantReport();
        report.setApplicationId(applicationId);
        report.setDetails(details);
        report.setOrganizerId(organizerId);
        
        ApplicantReport savedReport = applicantReportService.createReport(report, screenshot);
        return ResponseEntity.status(201).body(savedReport);
    }

    @GetMapping("/status/{applicationId}")
    public ResponseEntity<?> checkReportStatus(@PathVariable String applicationId, @AuthenticationPrincipal String organizerId) {
        boolean hasReported = applicantReportService.hasOrganizerReportedApplicant(applicationId, organizerId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasReported", hasReported);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ApplicantReport>> getAllReports() {
        return ResponseEntity.ok(applicantReportService.getAllReports());
    }
}
