package com.hariraam.jobportal.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class SeekerCertification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seeker_id")
    private JobSeeker seeker;

    private String certName;
    private String issuer;
    private String year;
}
