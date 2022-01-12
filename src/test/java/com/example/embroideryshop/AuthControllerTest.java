package com.example.embroideryshop;

import com.example.embroideryshop.exception.EmailInUseException;
import com.example.embroideryshop.model.User;
import com.example.embroideryshop.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

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
    private ObjectMapper objectMapper;

    @Test
    @Transactional
    public void shouldThrowEmailInUseException() throws Exception {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setUsername("test");
        user.setPassword("test");
        mockMvc.perform(post("/register")
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof EmailInUseException))
                .andExpect(result -> assertThat(result.getResolvedException().getMessage()).contains("Email 'test@gmail.com' jest zajęty"));
    }

    @Test
    @Transactional
    public void shouldRegisterUser() throws Exception {
        User user = new User();
        user.setEmail("testUser@test.test");
        user.setUsername("testUser");
        user.setPassword("testUser");
        mockMvc.perform(post("/register")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        User savedUser = userRepository.findUserByEmail("testUser@test.test");
        Assertions.assertEquals(savedUser.getUsername(), user.getUsername());
    }
}
