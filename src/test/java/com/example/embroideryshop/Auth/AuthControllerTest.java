package com.example.embroideryshop.Auth;

import com.example.embroideryshop.exception.EmailInUseException;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.RoleRepository;
import com.example.embroideryshop.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.embroideryshop.TestsHelperMethods.createTestUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanTestData() {
        User user = userRepository.findUserByEmail("testUser@test.test");
        if (user != null) {
            userRepository.delete(user);
        }
    }

    @Test
    public void shouldThrowEmailInUseException() throws Exception {
        User user = createTestUser();
        userRepository.save(user);
        User userWithEmailInUse = createTestUser();
        mockMvc.perform(post("/register")
                        .content(objectMapper.writeValueAsString(userWithEmailInUse))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof EmailInUseException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage())
                        .contains(String.format("Email '%s' jest zajęty", user.getEmail())));
    }

    @Test
    public void shouldRegisterUser() throws Exception {
        User user = createTestUser();
        mockMvc.perform(post("/register")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        User savedUser = userRepository.findUserByEmail("testUser@test.test");
        Assertions.assertEquals(savedUser.getUsername(), user.getUsername());
    }
}
