package com.hariraam.jobportal.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Getter
@Setter
@NoArgsConstructor


public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long user_id;
 
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$",message = "User Name must be alpha numeric")
    private String userName;

    @NotBlank(message="Name can't be blank")
    private String name;

    @NotBlank(message = "Email cant be Blank")
    @Email(message = "Enter the valid email address")
    @Column(unique=true,nullable=false)
    private String email;
    
    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

}
