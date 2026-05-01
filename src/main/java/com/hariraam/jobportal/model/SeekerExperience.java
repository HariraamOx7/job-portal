package com.hariraam.jobportal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class SeekerExperience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seeker_id")
    private JobSeeker seeker;

    private String company;
    private String title;
    private String startYear;
    private String endYear;

    @Column(columnDefinition = "TEXT")
    private String description;
}
