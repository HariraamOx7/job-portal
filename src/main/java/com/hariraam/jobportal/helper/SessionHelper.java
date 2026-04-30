package com.hariraam.jobportal.helper;

import com.hariraam.jobportal.model.JobRecruiter;
import com.hariraam.jobportal.model.JobSeeker;
import com.hariraam.jobportal.model.User;
import com.hariraam.jobportal.repository.JobRecruiterRepository;
import com.hariraam.jobportal.repository.JobSeekerRepository;
import com.hariraam.jobportal.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionHelper {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JobRecruiterRepository recruiterRepo;
    @Autowired
    private JobSeekerRepository seekerRepo;

    public User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return null;
        return userRepo.findById(userId).orElse(null);
    }

    public JobRecruiter getCurrentRecruiter(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) return null;
        return recruiterRepo.findByUser(user).orElseGet(() -> {
            JobRecruiter r = new JobRecruiter();
            r.setUser(user);
            r.setCompanyName(user.getName());
            r.setCompanyWebsite("");
            r.setCompanyEmail(user.getEmail());
            r.setIndustry("");
            r.setDesignation("");
            return recruiterRepo.save(r);
        });
    }

    public JobSeeker getCurrentSeeker(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) return null;
        return seekerRepo.findByUser(user).orElseGet(() -> {
            JobSeeker s = new JobSeeker();
            s.setUser(user);
            s.setLinkedinUrl("");
            s.setGithubUrl("");
            s.setPortfolioUrl("");
            // dateOfBirth left null; user should complete their profile
            return seekerRepo.save(s);
        });
    }
}
