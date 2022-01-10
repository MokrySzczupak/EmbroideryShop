package com.example.embroideryshop.controller;

import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;

    @PostMapping("/register")
    public String signup(@RequestBody User user) {
        return userDetailsService.signup(user);
    }
}
