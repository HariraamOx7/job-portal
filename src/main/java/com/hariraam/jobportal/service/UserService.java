package com.hariraam.jobportal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hariraam.jobportal.model.*;
import com.hariraam.jobportal.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    public User login(String email,String password){
          User user=userRepo.findByEmail(email);
          if(user!=null&&user.getPassword().equals(password)){
            return user;
          }
          return null;
    }
    public boolean saveUser(User user){
        User existingUser=userRepo.findByEmail(user.getEmail());
        if(existingUser!=null){
              return false;
        }
        userRepo.save(user);
        return true;
    }
}
