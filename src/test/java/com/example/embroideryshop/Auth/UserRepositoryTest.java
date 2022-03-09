package com.example.embroideryshop.Auth;

import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    public void shouldCreateUser() {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPassword");
        user.setEmail("tesUset@testUser.com");

        userRepository.save(user);

        User existUser = userRepository.findUserByEmail("tesUset@testUser.com");

        assertThat(existUser.getEmail()).isEqualTo(user.getEmail());
    }

}
