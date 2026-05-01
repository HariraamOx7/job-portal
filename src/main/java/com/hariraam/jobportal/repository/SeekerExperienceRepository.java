package com.hariraam.jobportal.repository;

import com.hariraam.jobportal.model.JobSeeker;
import com.hariraam.jobportal.model.SeekerExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SeekerExperienceRepository extends JpaRepository<SeekerExperience, Long> {
    List<SeekerExperience> findBySeeker(JobSeeker seeker);
}
