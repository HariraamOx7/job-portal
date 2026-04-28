package com.hariraam.jobportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

public class JobRecruiter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jrId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @NotBlank
    private String companyName;
    @NotBlank
    private String companyWebsite;
    @NotBlank
    @Email(message = "Enter a valid Email Address")
    private String companyEmail;
    
    
    private String companyAddress;
    @NotBlank
    private String industry;
    private Integer companySize;
    private String aboutCompany;
    @NotBlank
    private String designation;
}
