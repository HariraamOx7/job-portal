package com.hariraam.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hariraam.jobportal.model.JobSeeker;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker,Long> {
    
}
