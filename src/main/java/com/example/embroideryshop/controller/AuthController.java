package com.example.embroideryshop.controller;

import com.example.embroideryshop.config.LoginCredentials;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/auth/signup")
    public String signup(@RequestBody User user) {
        return userDetailsService.signup(user);
    }
}
