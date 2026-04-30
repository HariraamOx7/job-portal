package com.hariraam.jobportal.repository;

import com.hariraam.jobportal.model.Job;
import com.hariraam.jobportal.model.JobRecruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByRecruiter(JobRecruiter recruiter);
    List<Job> findByStatus(String status);
    List<Job> findByStatusOrderByPostedAtDesc(String status);
}
