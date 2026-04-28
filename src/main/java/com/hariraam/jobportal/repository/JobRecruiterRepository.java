package com.hariraam.jobportal.repository;

import com.hariraam.jobportal.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRecruiterRepository extends JpaRepository<JobRecruiter,Long> {
    
}
