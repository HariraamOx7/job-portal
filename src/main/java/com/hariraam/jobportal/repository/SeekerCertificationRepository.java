package com.hariraam.jobportal.repository;

import com.hariraam.jobportal.model.JobSeeker;
import com.hariraam.jobportal.model.SeekerCertification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeekerCertificationRepository extends JpaRepository<SeekerCertification, Long> {
    List<SeekerCertification> findBySeeker(JobSeeker seeker);
}
