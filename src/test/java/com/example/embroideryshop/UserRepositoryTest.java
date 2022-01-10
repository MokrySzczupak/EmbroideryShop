package com.example.embroideryshop;

import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

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
