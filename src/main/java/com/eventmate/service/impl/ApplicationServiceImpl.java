package com.eventmate.service.impl;

import com.eventmate.model.Application;
import com.eventmate.repository.ApplicationRepository;
import com.eventmate.service.ApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationServiceImpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public Application submitApplication(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public List<Application> getApplicationsForOrganizer(String organizerId) {
        return applicationRepository.findByOrganizerIdOrderByCreatedAtDesc(organizerId);
    }
}
