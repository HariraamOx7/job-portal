package com.hariraam.jobportal.controller;

import com.hariraam.jobportal.helper.SessionHelper;
import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.service.JobApplicationService;
import com.hariraam.jobportal.service.JobService;
import com.hariraam.jobportal.service.SeekerProfileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/JobPortal/seeker")
public class SeekerController {

    @Autowired private SessionHelper sessionHelper;
    @Autowired private JobService jobService;
    @Autowired private JobApplicationService appService;
    @Autowired private SeekerProfileService profileService;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    @GetMapping("/home")
    public String home(HttpSession session, Model model,
                       @RequestParam(required = false) String title,
                       @RequestParam(required = false) String company,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) String jobType,
                       @RequestParam(required = false) String experienceRequired,
                       @RequestParam(required = false) String skills) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";

        boolean hasFilter = (title != null && !title.isBlank())
                || (company != null && !company.isBlank())
                || (location != null && !location.isBlank())
                || (jobType != null && !jobType.isBlank())
                || (experienceRequired != null && !experienceRequired.isBlank())
                || (skills != null && !skills.isBlank());

        List<Job> jobs = hasFilter
                ? jobService.searchJobs(title, company, location, jobType, experienceRequired, skills)
                : jobService.getOpenJobs();

        model.addAttribute("seeker", seeker);
        model.addAttribute("jobs", jobs);
        model.addAttribute("title", title);
        model.addAttribute("company", company);
        model.addAttribute("location", location);
        model.addAttribute("jobType", jobType);
        model.addAttribute("experienceRequired", experienceRequired);
        model.addAttribute("skills", skills);
        return "seeker/home";
    }

    @GetMapping("/job/{id}")
    public String jobDetail(@PathVariable Long id, HttpSession session, Model model) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        Optional<Job> jobOpt = jobService.getJobById(id);
        if (jobOpt.isEmpty()) return "redirect:/JobPortal/seeker/home";
        Job job = jobOpt.get();
        boolean hasApplied = appService.hasApplied(job, seeker);
        ApplicationStatus appStatus = null;
        String rejectionReason = null;
        if (hasApplied) {
            Optional<JobApplication> appOpt = appService.getApplicationByJobAndSeeker(job, seeker);
            appStatus = appOpt.map(JobApplication::getStatus).orElse(null);
            rejectionReason = appOpt.map(JobApplication::getRejectionReason).orElse(null);
        }
        int applicantCount = appService.getApplicationsByJob(job).size();
        model.addAttribute("job", job);
        model.addAttribute("seeker", seeker);
        model.addAttribute("hasApplied", hasApplied);
        model.addAttribute("appStatus", appStatus);
        model.addAttribute("rejectionReason", rejectionReason);
        model.addAttribute("applicantCount", applicantCount);
        return "seeker/job-detail";
    }

    @PostMapping("/job/{id}/apply")
    public String apply(@PathVariable Long id, HttpSession session,
                        @RequestParam(required = false) MultipartFile resumeFile) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        Optional<Job> jobOpt = jobService.getJobById(id);
        jobOpt.ifPresent(job -> appService.apply(job, seeker, resumeFile));
        return "redirect:/JobPortal/seeker/job/" + id;
    }

    @GetMapping("/job-history")
    public String jobHistory(HttpSession session, Model model) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        List<JobApplication> applications = appService.getApplicationsBySeeker(seeker);
        model.addAttribute("seeker", seeker);
        model.addAttribute("applications", applications);
        return "seeker/job-history";
    }

    // ---- Profile ----

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        model.addAttribute("seeker", seeker);
        model.addAttribute("experiences", profileService.getExperiences(seeker));
        model.addAttribute("certifications", profileService.getCertifications(seeker));
        return "seeker/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfileForm(HttpSession session, Model model) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        model.addAttribute("seeker", seeker);
        return "seeker/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String saveProfile(HttpSession session,
                               @RequestParam(required = false) String headline,
                               @RequestParam(required = false) String about,
                               @RequestParam(required = false) String skills,
                               @RequestParam(required = false) Integer yearsOfExperience,
                               @RequestParam(required = false) String city,
                               @RequestParam(required = false) String state,
                               @RequestParam(required = false) String country,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) String linkedinUrl,
                               @RequestParam(required = false) String githubUrl,
                               @RequestParam(required = false) String portfolioUrl,
                               @RequestParam(required = false) String degree,
                               @RequestParam(required = false) String department,
                               @RequestParam(required = false) String collegeName,
                               @RequestParam(required = false) Double cgpa,
                               @RequestParam(required = false) Integer graduationYear) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        if (headline != null) seeker.setHeadline(headline);
        if (about != null) seeker.setAbout(about);
        if (skills != null) seeker.setSkills(skills);
        if (yearsOfExperience != null) seeker.setYearsOfExperience(yearsOfExperience);
        if (city != null) seeker.setCity(city);
        if (state != null) seeker.setState(state);
        if (country != null) seeker.setCountry(country);
        if (phoneNumber != null && !phoneNumber.isBlank()) seeker.setPhoneNumber(phoneNumber);
        seeker.setLinkedinUrl(linkedinUrl != null ? linkedinUrl : "");
        seeker.setGithubUrl(githubUrl != null ? githubUrl : "");
        seeker.setPortfolioUrl(portfolioUrl != null ? portfolioUrl : "");
        if (degree != null) seeker.setDegree(degree);
        if (department != null) seeker.setDepartment(department);
        if (collegeName != null) seeker.setCollegeName(collegeName);
        if (cgpa != null) seeker.setCgpa(cgpa);
        if (graduationYear != null) seeker.setGraduationYear(graduationYear);
        profileService.saveSeeker(seeker);
        return "redirect:/JobPortal/seeker/profile";
    }

    @PostMapping("/profile/experience/add")
    public String addExperience(HttpSession session,
                                 @RequestParam String company,
                                 @RequestParam String title,
                                 @RequestParam(required = false) String startYear,
                                 @RequestParam(required = false) String endYear,
                                 @RequestParam(required = false) String description) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        profileService.addExperience(seeker, company, title, startYear, endYear, description);
        return "redirect:/JobPortal/seeker/profile";
    }

    @PostMapping("/profile/experience/{id}/delete")
    public String deleteExperience(@PathVariable Long id, HttpSession session) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        profileService.deleteExperience(id, seeker);
        return "redirect:/JobPortal/seeker/profile";
    }

    @PostMapping("/profile/certification/add")
    public String addCertification(HttpSession session,
                                    @RequestParam String certName,
                                    @RequestParam(required = false) String issuer,
                                    @RequestParam(required = false) String year) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        profileService.addCertification(seeker, certName, issuer, year);
        return "redirect:/JobPortal/seeker/profile";
    }

    @PostMapping("/profile/certification/{id}/delete")
    public String deleteCertification(@PathVariable Long id, HttpSession session) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        profileService.deleteCertification(id, seeker);
        return "redirect:/JobPortal/seeker/profile";
    }
}
