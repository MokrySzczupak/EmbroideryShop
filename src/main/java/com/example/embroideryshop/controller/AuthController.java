package com.example.embroideryshop.controller;

import com.example.embroideryshop.controller.dto.UserProfileDto;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public String signup(@RequestBody User user) {
        return userDetailsService.signup(user);
    }

    @GetMapping("/profile/details")
    public UserProfileDto getUserProfileDetails(Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        return UserProfileDto.from(user);
    }
}
