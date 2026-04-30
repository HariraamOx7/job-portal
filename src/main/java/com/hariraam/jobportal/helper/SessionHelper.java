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
        return recruiterRepo.findByUser(user).orElse(null);
    }

    public JobSeeker getCurrentSeeker(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) return null;
        return seekerRepo.findByUser(user).orElse(null);
    }
}
