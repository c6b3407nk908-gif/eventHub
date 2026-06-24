package com.eventmate.service;

import com.eventmate.model.Application;
import java.util.List;

public interface ApplicationService {
    Application submitApplication(Application application);
    List<Application> getApplicationsForOrganizer(String organizerId);
}
