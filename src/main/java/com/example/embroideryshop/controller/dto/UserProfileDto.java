package com.example.embroideryshop.controller.dto;

import com.example.embroideryshop.model.User;
import lombok.Data;

@Data
public class UserProfileDto {
    private String email;
    private String username;

    public static UserProfileDto from(User user) {
        UserProfileDto userProfile = new UserProfileDto();
        userProfile.email = user.getEmail();
        userProfile.username = user.getUsername();
        return userProfile;
    }
}
