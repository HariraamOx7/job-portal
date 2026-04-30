package com.hariraam.jobportal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "seeker_id"}))
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "seeker_id")
    private JobSeeker seeker;

    private LocalDateTime appliedAt;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @PrePersist
    public void prePersist() {
        if (appliedAt == null) appliedAt = LocalDateTime.now();
        if (status == null) status = ApplicationStatus.APPLIED;
    }
}
