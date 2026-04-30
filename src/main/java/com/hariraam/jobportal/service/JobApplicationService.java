package com.hariraam.jobportal.service;

import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository appRepo;

    public boolean apply(Job job, JobSeeker seeker) {
        if (appRepo.findByJobAndSeeker(job, seeker).isPresent()) {
            return false;
        }
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setStatus(ApplicationStatus.APPLIED);
        appRepo.save(application);
        return true;
    }

    public List<JobApplication> getApplicationsByJob(Job job) {
        return appRepo.findByJob(job);
    }

    public List<JobApplication> getApplicationsBySeeker(JobSeeker seeker) {
        return appRepo.findBySeeker(seeker);
    }

    public void acceptApplicant(Long applicationId) {
        appRepo.findById(applicationId).ifPresent(app -> {
            app.setStatus(ApplicationStatus.ACCEPTED);
            appRepo.save(app);
        });
    }

    public boolean hasApplied(Job job, JobSeeker seeker) {
        return appRepo.findByJobAndSeeker(job, seeker).isPresent();
    }

    public void rejectApplicant(Long applicationId) {
        appRepo.findById(applicationId).ifPresent(app -> {
            app.setStatus(ApplicationStatus.REJECTED);
            appRepo.save(app);
        });
    }

    public Optional<JobApplication> getApplicationByJobAndSeeker(Job job, JobSeeker seeker) {
        return appRepo.findByJobAndSeeker(job, seeker);
    }
}
