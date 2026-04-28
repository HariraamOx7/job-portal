package com.hariraam.jobportal.controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hariraam.jobportal.service.UserService;
import com.hariraam.jobportal.model.*;


@Controller
@RequestMapping("/JobPortal")

public class AuthController {
    @Autowired
    private  UserService userService;
    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
    @PostMapping("/login")
    public String loginPage(@RequestParam String email,@RequestParam String password,Model model){
        User user=userService.login(email, password);
        if(user==null){
             model.addAttribute("error","Invalid Email or Password ");
               return "login";
        }
        if(user.getRole()==Role.JOB_SEEKER){
            return "redirect:/JobPortal/seeker/home";
        }
        else if(user.getRole()==Role.JOB_RECRUITER){
            return "redirect:/JobPortal/recruiter/home";
        }
        model.addAttribute("error", "user Role not recognized");
        return "login";
    }
    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }
    @PostMapping("/register")
    public String register(@ModelAttribute User user,Model model){
        if(userService.saveUser(user)){
            return "redirect:/JobPortal/login";
        }
        model.addAttribute("error","User may already exist!");
        return "register";
    }
}
