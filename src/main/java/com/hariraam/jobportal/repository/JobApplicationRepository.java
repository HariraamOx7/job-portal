package com.hariraam.jobportal.repository;

import com.hariraam.jobportal.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJob(Job job);
    List<JobApplication> findBySeeker(JobSeeker seeker);
    Optional<JobApplication> findByJobAndSeeker(Job job, JobSeeker seeker);
}
