package com.hariraam.jobportal.service;

import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.repository.JobApplicationRepository;
import com.hariraam.jobportal.repository.JobRecruiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobApplicationService {

    @Autowired
    private JobApplicationRepository applicationRepository;

    @Autowired
    private JobRecruiterRepository recruiterRepository;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    public List<JobApplication> getApplicationsByJob(Job job) {
        return applicationRepository.findByJob(job);
    }

    public List<JobApplication> getApplicationsBySeeker(JobSeeker seeker) {
        return applicationRepository.findBySeeker(seeker);
    }

    public Optional<JobApplication> getApplicationByJobAndSeeker(Job job, JobSeeker seeker) {
        return applicationRepository.findByJobAndSeeker(job, seeker);
    }
    @Transactional
    public Optional<JobApplication> getApplicationById(Long id) {
        return applicationRepository.findById(id);
    }

    public boolean hasApplied(Job job, JobSeeker seeker) {
        return applicationRepository.findByJobAndSeeker(job, seeker).isPresent();
    }

    public void apply(Job job, JobSeeker seeker, MultipartFile resumeFile) {
        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setSeeker(seeker);
        application.setStatus(ApplicationStatus.APPLIED);

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                // Get absolute path from project root or a fixed location
                String baseDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "resumes";
                File dir = new File(baseDir);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    System.out.println("Created upload directory: " + dir.getAbsolutePath() + " -> " + created);
                }
                String fileName = UUID.randomUUID().toString() + "_" + resumeFile.getOriginalFilename();
                String filePath = baseDir + File.separator + fileName;
                File destFile = new File(filePath);
                resumeFile.transferTo(destFile);
                application.setApplicationResumePath(destFile.getAbsolutePath());
                System.out.println("Resume saved to: " + destFile.getAbsolutePath());
            } catch (IOException e) {
                System.err.println("File upload failed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No file uploaded or file is empty");
        }
        applicationRepository.save(application);
    }

    // ----- Existing offer/accept/reject methods adjusted to use new fields -----
    public void offerApplicant(Long applicationId) {
        JobApplication app = applicationRepository.findById(applicationId).orElseThrow();
        app.setStatus(ApplicationStatus.OFFERED);
        app.setOfferDate(LocalDate.now());
        applicationRepository.save(app);
    }

    public void acceptApplicant(Long applicationId) {
        JobApplication app = applicationRepository.findById(applicationId).orElseThrow();
        app.setStatus(ApplicationStatus.ACCEPTED);

        applicationRepository.save(app);
    }

    public void rejectApplicant(Long applicationId, String reason) {
        JobApplication app = applicationRepository.findById(applicationId).orElseThrow();
        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason(reason);
        app.setOfferAccepted(false);
        applicationRepository.save(app);
    }

    // ----- New: Schedule Interview -----
    public void scheduleInterview(Long applicationId, LocalDateTime interviewDate,
                                  String interviewLocation, String interviewNotes) {
        JobApplication app = applicationRepository.findById(applicationId).orElseThrow();
        app.setInterviewDate(interviewDate);
        app.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        // Optionally store location/notes – you need to add fields to JobApplication model
        // For now we ignore them, but you can extend the model.
        applicationRepository.save(app);
    }

    // ----- New: Accept/Reject Offer from Seeker side -----
    public void acceptOffer(Long applicationId) {
        JobApplication app = applicationRepository.findById(applicationId).orElseThrow();
        app.setStatus(ApplicationStatus.ACCEPTED);
        app.setOfferAccepted(true);
        app.setOfferDate(LocalDate.now());
        applicationRepository.save(app);
    }

    public void rejectOffer(Long applicationId, String reason) {
        JobApplication app = applicationRepository.findById(applicationId).orElseThrow();
        app.setStatus(ApplicationStatus.REJECTED);
        app.setOfferAccepted(false);
        app.setRejectionReason(reason);
        applicationRepository.save(app);
    }

    // ----- Helper for Recruiter profile save (if needed) -----
    public void saveRecruiter(JobRecruiter recruiter) {
        recruiterRepository.save(recruiter);
    }
}