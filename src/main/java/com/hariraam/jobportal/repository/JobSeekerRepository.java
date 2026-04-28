package com.hariraam.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hariraam.jobportal.model.JobSeeker;

public interface JobSeekerRepository extends JpaRepository<JobSeeker,Long> {
    
}
