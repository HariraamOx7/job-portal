package com.hariraam.jobportal.service;

import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.repository.JobApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobApplicationService {
    @Autowired
    private JobApplicationRepository appRepo;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    public boolean apply(Job job, JobSeeker seeker, MultipartFile resumeFile) {
        if (appRepo.findByJobAndSeeker(job, seeker).isPresent()) {
            return false;
        }
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setStatus(ApplicationStatus.APPLIED);

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                Path dir = Paths.get(uploadDir);
                Files.createDirectories(dir);
                String filename = UUID.randomUUID() + "_" + resumeFile.getOriginalFilename();
                Path dest = dir.resolve(filename);
                resumeFile.transferTo(dest.toFile());
                application.setApplicationResumePath(dest.toString());
            } catch (IOException e) {
                // log and continue without resume
            }
        }

        appRepo.save(application);
        return true;
    }

    public List<JobApplication> getApplicationsByJob(Job job) {
        return appRepo.findByJob(job);
    }

    public List<JobApplication> getApplicationsBySeeker(JobSeeker seeker) {
        return appRepo.findBySeeker(seeker);
    }

    public void offerApplicant(Long applicationId) {
        appRepo.findById(applicationId).ifPresent(app -> {
            app.setStatus(ApplicationStatus.OFFERED);
            appRepo.save(app);
        });
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

    public void rejectApplicant(Long applicationId, String reason) {
        appRepo.findById(applicationId).ifPresent(app -> {
            app.setStatus(ApplicationStatus.REJECTED);
            app.setRejectionReason(reason);
            appRepo.save(app);
        });
    }

    public Optional<JobApplication> getApplicationByJobAndSeeker(Job job, JobSeeker seeker) {
        return appRepo.findByJobAndSeeker(job, seeker);
    }

    public Optional<JobApplication> getApplicationById(Long id) {
        return appRepo.findById(id);
    }
}
