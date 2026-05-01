package com.hariraam.jobportal.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;


@Entity
@Setter
@Getter
@NoArgsConstructor
public class JobSeeker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long js_id;

    @OneToOne
    @JoinColumn(name = "user_id",unique = true)
    private User user; 

    
    private String headline;
    private String summary;

    private String degree;
    private String department;
    private String collegeName;
    private Double cgpa;
    private Integer graduationYear;
    
    private String city;
    private String state;
    private String country;


    private String linkedinUrl;

    private String githubUrl;

    private String portfolioUrl;


    private String gender;

    private LocalDate dateOfBirth;


    private String phoneNumber;

    private String resumePath;
} 
