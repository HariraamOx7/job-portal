package com.hariraam.jobportal.model;

import java.time.LocalDate;
import jakarta.annotation.*;
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

    @NotEmpty
    private String linkedinUrl;
    @NotEmpty
    private String githubUrl;
    @NotEmpty
    private String portfolioUrl;


    private String gender;
    @NotBlank(message = "Age cant be Blank")
    private LocalDate dateOfBirth;

    @Pattern(regexp="^[0-9]{10}$",message = "Phone number must be exactly 10 digits with value(0-9)")
    private String phoneNumber;

    private String resumePath;
} 
