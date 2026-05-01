package com.hariraam.jobportal.repository;

import com.hariraam.jobportal.model.Job;
import com.hariraam.jobportal.model.JobRecruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByRecruiter(JobRecruiter recruiter);
    List<Job> findByStatus(String status);
    List<Job> findByStatusOrderByPostedAtDesc(String status);

    @Query("SELECT j FROM Job j WHERE j.status = 'OPEN' " +
           "AND (:title IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:company IS NULL OR LOWER(j.company) LIKE LOWER(CONCAT('%', :company, '%'))) " +
           "AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:jobType IS NULL OR j.jobType = :jobType) " +
           "AND (:experienceRequired IS NULL OR j.experienceRequired = :experienceRequired) " +
           "AND (:skills IS NULL OR LOWER(j.requiredSkills) LIKE LOWER(CONCAT('%', :skills, '%'))) " +
           "ORDER BY j.postedAt DESC")
    List<Job> searchJobs(@Param("title") String title,
                         @Param("company") String company,
                         @Param("location") String location,
                         @Param("jobType") String jobType,
                         @Param("experienceRequired") String experienceRequired,
                         @Param("skills") String skills);
}
