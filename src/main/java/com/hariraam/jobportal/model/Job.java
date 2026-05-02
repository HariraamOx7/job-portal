package com.hariraam.jobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    private JobRecruiter recruiter;

    private String title;
    private String company;
    private String location;
    private String jobType;
    private String salaryRange;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String requiredSkills;

    private String experienceRequired;

    private LocalDateTime postedAt;

    private String status = "OPEN";

    @PrePersist
    public void prePersist() {
        if (postedAt == null) postedAt = LocalDateTime.now();
        if (status == null) status = "OPEN";
    }
}
