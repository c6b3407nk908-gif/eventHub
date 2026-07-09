package com.eventmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.eventmate.repository.ApplicantReportRepository;
import com.eventmate.repository.ApplicationRepository;
import com.eventmate.model.ApplicantReport;
import com.eventmate.model.Application;
import java.util.Optional;
import java.util.List;

@SpringBootApplication
public class EventhubApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventhubApplication.class, args);
    }

    @Bean
    public CommandLineRunner backfillApplicantReports(ApplicantReportRepository reportRepo, ApplicationRepository appRepo) {
        return args -> {
            List<ApplicantReport> reports = reportRepo.findAll();
            boolean updated = false;
            for (ApplicantReport report : reports) {
                if (report.getApplicantEmail() == null || report.getApplicantEmail().isEmpty()) {
                    if (report.getApplicationId() != null) {
                        Optional<Application> appOpt = appRepo.findById(report.getApplicationId());
                        if (appOpt.isPresent()) {
                            report.setApplicantEmail(appOpt.get().getApplicantEmail());
                            report.setEventId(appOpt.get().getEventId());
                            reportRepo.save(report);
                            updated = true;
                        }
                    }
                }
            }
            if (updated) {
                System.out.println("Successfully backfilled missing applicant emails in old reports.");
            }
        };
    }
}
