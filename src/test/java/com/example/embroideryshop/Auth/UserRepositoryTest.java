package com.example.embroideryshop.Auth;

import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.embroideryshop.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final String testUserEmail = "testUser@test.test";

    @BeforeEach
    public void cleanTestData() {
        User user = userRepository.findUserByEmail(testUserEmail);
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Test
    public void shouldCreateUser() {
        // given
        User user = createTestUser();
        // when
        userRepository.save(user);
        // then
        User existUser = userRepository.findUserByEmail(testUserEmail);
        assertThat(existUser.getEmail()).isEqualTo(user.getEmail());
    }

}
