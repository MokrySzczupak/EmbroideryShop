package com.example.embroideryshop.controller;

import com.example.embroideryshop.controller.dto.UserProfileDto;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.service.AuthService;
import com.example.embroideryshop.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthService authService;

    @PostMapping("/register")
    public String signup(@RequestBody User user) {
        return userDetailsService.signup(user);
    }

    @GetMapping("/profile/details")
    public UserProfileDto getUserProfileDetails(Authentication auth) {
        User user = userDetailsService.loadLoggedUser(auth);
        return UserProfileDto.from(user);
    }

    @PostMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        authService.refreshToken(request, response);
    }
}
