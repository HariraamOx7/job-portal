package com.hariraam.jobportal.service;

import com.hariraam.jobportal.model.JobSeeker;
import com.hariraam.jobportal.model.SeekerCertification;
import com.hariraam.jobportal.model.SeekerExperience;
import com.hariraam.jobportal.repository.JobSeekerRepository;
import com.hariraam.jobportal.repository.SeekerCertificationRepository;
import com.hariraam.jobportal.repository.SeekerExperienceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeekerProfileService {

    @Autowired private JobSeekerRepository seekerRepo;
    @Autowired private SeekerExperienceRepository expRepo;
    @Autowired private SeekerCertificationRepository certRepo;

    public void saveSeeker(JobSeeker seeker) {
        seekerRepo.save(seeker);
    }

    public List<SeekerExperience> getExperiences(JobSeeker seeker) {
        return expRepo.findBySeeker(seeker);
    }


    public JobSeeker getSeekerById(Long seekerId) {
        return seekerRepo.findById(seekerId).orElse(null);
    }

    public void addExperience(JobSeeker seeker, String company, String title,
                               String startYear, String endYear, String description) {
        SeekerExperience exp = new SeekerExperience();
        exp.setSeeker(seeker);
        exp.setCompany(company);
        exp.setTitle(title);
        exp.setStartYear(startYear);
        exp.setEndYear(endYear);
        exp.setDescription(description);
        expRepo.save(exp);
    }

    public void deleteExperience(Long id, JobSeeker seeker) {
        expRepo.findById(id).ifPresent(exp -> {
            if (exp.getSeeker().getJs_id().equals(seeker.getJs_id())) {
                expRepo.delete(exp);
            }
        });
    }

    public List<SeekerCertification> getCertifications(JobSeeker seeker) {
        return certRepo.findBySeeker(seeker);
    }

    public void addCertification(JobSeeker seeker, String certName, String issuer, String year) {
        SeekerCertification cert = new SeekerCertification();
        cert.setSeeker(seeker);
        cert.setCertName(certName);
        cert.setIssuer(issuer);
        cert.setYear(year);
        certRepo.save(cert);
    }

    public void deleteCertification(Long id, JobSeeker seeker) {
        certRepo.findById(id).ifPresent(cert -> {
            if (cert.getSeeker().getJs_id().equals(seeker.getJs_id())) {
                certRepo.delete(cert);
            }
        });
    }

    public Optional<SeekerExperience> getExperienceById(Long id) {
        return expRepo.findById(id);
    }

    public Optional<SeekerCertification> getCertificationById(Long id) {
        return certRepo.findById(id);
    }
}
