package com.hariraam.jobportal.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hariraam.jobportal.model.User;
public interface UserRepository extends JpaRepository<User,Long>{
    
}
