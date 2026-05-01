package com.hariraam.jobportal.controller;

import com.hariraam.jobportal.helper.SessionHelper;
import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.service.JobApplicationService;
import com.hariraam.jobportal.service.JobService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/JobPortal/recruiter")
public class RecruiterController {

    @Autowired private SessionHelper sessionHelper;
    @Autowired private JobService jobService;
    @Autowired private JobApplicationService appService;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        List<Job> jobs = jobService.getJobsByRecruiter(recruiter);
        long totalApplicants = jobs.stream()
            .mapToLong(j -> appService.getApplicationsByJob(j).size())
            .sum();
        model.addAttribute("recruiter", recruiter);
        model.addAttribute("jobs", jobs);
        model.addAttribute("totalJobs", jobs.size());
        model.addAttribute("totalApplicants", totalApplicants);
        return "recruiter/home";
    }

    @GetMapping("/post-job")
    public String postJobForm(HttpSession session, Model model) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        Job job = new Job();
        job.setCompany(recruiter.getCompanyName());
        model.addAttribute("job", job);
        model.addAttribute("recruiter", recruiter);
        return "recruiter/post-job";
    }

    @PostMapping("/post-job")
    public String postJob(HttpSession session, @ModelAttribute Job job) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        job.setRecruiter(recruiter);
        job.setCompany(recruiter.getCompanyName());
        jobService.saveJob(job);
        return "redirect:/JobPortal/recruiter/manage-jobs";
    }

    @GetMapping("/manage-jobs")
    public String manageJobs(HttpSession session, Model model) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        List<Job> jobs = jobService.getJobsByRecruiter(recruiter);
        Map<Long, Integer> applicantCounts = new HashMap<>();
        for (Job job : jobs) {
            applicantCounts.put(job.getId(), appService.getApplicationsByJob(job).size());
        }
        model.addAttribute("jobs", jobs);
        model.addAttribute("recruiter", recruiter);
        model.addAttribute("applicantCounts", applicantCounts);
        return "recruiter/manage-jobs";
    }

    @GetMapping("/job/{id}/applicants")
    public String viewApplicants(@PathVariable Long id, HttpSession session, Model model) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        Optional<Job> jobOpt = jobService.getJobById(id);
        if (jobOpt.isEmpty()) return "redirect:/JobPortal/recruiter/manage-jobs";
        Job job = jobOpt.get();
        List<JobApplication> applications = appService.getApplicationsByJob(job);
        model.addAttribute("job", job);
        model.addAttribute("applications", applications);
        model.addAttribute("recruiter", recruiter);
        return "recruiter/applicants";
    }

    @PostMapping("/job/{applicationId}/offer")
    public String offerApplicant(@PathVariable Long applicationId, HttpSession session,
                                  @RequestParam Long jobId) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        appService.offerApplicant(applicationId);
        return "redirect:/JobPortal/recruiter/job/" + jobId + "/applicants";
    }

    @PostMapping("/job/{applicationId}/accept")
    public String acceptApplicant(@PathVariable Long applicationId, HttpSession session,
                                   @RequestParam Long jobId) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        appService.acceptApplicant(applicationId);
        return "redirect:/JobPortal/recruiter/job/" + jobId + "/applicants";
    }

    @PostMapping("/job/{applicationId}/reject")
    public String rejectApplicant(@PathVariable Long applicationId, HttpSession session,
                                   @RequestParam Long jobId,
                                   @RequestParam(required = false) String rejectionReason) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        appService.rejectApplicant(applicationId, rejectionReason);
        return "redirect:/JobPortal/recruiter/job/" + jobId + "/applicants";
    }

    @GetMapping("/application/{applicationId}/resume")
    public ResponseEntity<Resource> downloadResume(@PathVariable Long applicationId, HttpSession session) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return ResponseEntity.status(403).build();
        Optional<JobApplication> appOpt = appService.getApplicationById(applicationId);
        if (appOpt.isEmpty()) return ResponseEntity.notFound().build();
        JobApplication application = appOpt.get();
        String path = application.getApplicationResumePath();
        if (path == null || path.isBlank()) return ResponseEntity.notFound().build();
        File file = new File(path);
        if (!file.exists()) return ResponseEntity.notFound().build();
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/job/{id}/edit")
    public String editJobForm(@PathVariable Long id, HttpSession session, Model model) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        Optional<Job> jobOpt = jobService.getJobById(id);
        if (jobOpt.isEmpty()) return "redirect:/JobPortal/recruiter/manage-jobs";
        model.addAttribute("job", jobOpt.get());
        model.addAttribute("recruiter", recruiter);
        return "recruiter/edit-job";
    }

    @PostMapping("/job/{id}/edit")
    public String editJob(@PathVariable Long id, HttpSession session, @ModelAttribute Job job) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        Optional<Job> existing = jobService.getJobById(id);
        if (existing.isEmpty()) return "redirect:/JobPortal/recruiter/manage-jobs";
        Job existingJob = existing.get();
        existingJob.setTitle(job.getTitle());
        existingJob.setLocation(job.getLocation());
        existingJob.setJobType(job.getJobType());
        existingJob.setSalaryRange(job.getSalaryRange());
        existingJob.setDescription(job.getDescription());
        existingJob.setRequirements(job.getRequirements());
        existingJob.setRequiredSkills(job.getRequiredSkills());
        existingJob.setExperienceRequired(job.getExperienceRequired());
        jobService.updateJob(existingJob);
        return "redirect:/JobPortal/recruiter/manage-jobs";
    }

    @PostMapping("/job/{id}/close")
    public String closeJob(@PathVariable Long id, HttpSession session) {
        JobRecruiter recruiter = sessionHelper.getCurrentRecruiter(session);
        if (recruiter == null) return "redirect:/JobPortal/login";
        jobService.closeJob(id);
        return "redirect:/JobPortal/recruiter/manage-jobs";
    }
}
