package com.eventmate.repository;

import com.eventmate.model.ApplicantReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicantReportRepository extends MongoRepository<ApplicantReport, String> {
    List<ApplicantReport> findByApplicationId(String applicationId);
    boolean existsByApplicationIdAndOrganizerId(String applicationId, String organizerId);
    List<ApplicantReport> findByOrderByCreatedAtDesc();
}
