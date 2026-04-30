package com.hariraam.jobportal.service;

import com.hariraam.jobportal.model.Job;
import com.hariraam.jobportal.model.JobRecruiter;
import com.hariraam.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepo;

    public Job saveJob(Job job) {
        return jobRepo.save(job);
    }

    public List<Job> getOpenJobs() {
        return jobRepo.findByStatusOrderByPostedAtDesc("OPEN");
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepo.findById(id);
    }

    public List<Job> getJobsByRecruiter(JobRecruiter recruiter) {
        return jobRepo.findByRecruiter(recruiter);
    }

    public void closeJob(Long id) {
        jobRepo.findById(id).ifPresent(job -> {
            job.setStatus("CLOSED");
            jobRepo.save(job);
        });
    }

    public Job updateJob(Job job) {
        return jobRepo.save(job);
    }

    public void deleteJob(Long id) {
        jobRepo.deleteById(id);
    }
}
