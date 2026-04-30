package com.hariraam.jobportal.controller;

import com.hariraam.jobportal.helper.SessionHelper;
import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.service.JobApplicationService;
import com.hariraam.jobportal.service.JobService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/JobPortal/seeker")
public class SeekerController {

    @Autowired private SessionHelper sessionHelper;
    @Autowired private JobService jobService;
    @Autowired private JobApplicationService appService;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        List<Job> jobs = jobService.getOpenJobs();
        model.addAttribute("seeker", seeker);
        model.addAttribute("jobs", jobs);
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
        if (hasApplied) {
            appStatus = appService.getApplicationByJobAndSeeker(job, seeker)
                .map(JobApplication::getStatus).orElse(null);
        }
        model.addAttribute("job", job);
        model.addAttribute("seeker", seeker);
        model.addAttribute("hasApplied", hasApplied);
        model.addAttribute("appStatus", appStatus);
        return "seeker/job-detail";
    }

    @PostMapping("/job/{id}/apply")
    public String apply(@PathVariable Long id, HttpSession session) {
        JobSeeker seeker = sessionHelper.getCurrentSeeker(session);
        if (seeker == null) return "redirect:/JobPortal/login";
        Optional<Job> jobOpt = jobService.getJobById(id);
        jobOpt.ifPresent(job -> appService.apply(job, seeker));
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
}
