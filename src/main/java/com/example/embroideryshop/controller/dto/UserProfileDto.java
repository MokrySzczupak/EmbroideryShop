package com.example.embroideryshop.controller.dto;

import com.example.embroideryshop.model.Role;
import com.example.embroideryshop.model.User;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class UserProfileDto {
    private String email;
    private String username;
    private Set<String> roles;

    public static UserProfileDto from(User user) {
        UserProfileDto userProfile = new UserProfileDto();
        userProfile.email = user.getEmail();
        userProfile.username = user.getUsername();
        userProfile.roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return userProfile;
    }
}
