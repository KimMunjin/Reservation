package com.zerobase.reservation.service;

import com.zerobase.reservation.dto.LoginRequest;
import com.zerobase.reservation.dto.UserDTO;
import com.zerobase.reservation.entity.User;
import com.zerobase.reservation.repository.UserRepository;
import com.zerobase.reservation.type.UserType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void signUp() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testuser");
        userDTO.setPassword("password");
        userDTO.setUserType(UserType.USER);

        when(userRepository.existsByUsername(userDTO.getUsername())).thenReturn(false);

        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("hashedPassword");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L);
            return user;
        });

        User savedUser = userService.signUp(userDTO);

        assertNotNull(savedUser);
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("hashedPassword", savedUser.getPassword());
        assertEquals(UserType.USER, savedUser.getUserType());

    }

    @Test
    void authenticate() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        User user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setPassword("hashedPassword");
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        User authenticatedUser = userService.authenticate(loginRequest);

        assertNotNull(authenticatedUser);
        assertEquals(1L, authenticatedUser.getUserId());
        assertEquals("testuser", authenticatedUser.getUsername());
    }
}