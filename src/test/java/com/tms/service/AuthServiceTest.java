package com.tms.service;

import com.tms.dto.request.RegisterRequest;
import com.tms.exception.DuplicateResourceException;
import com.tms.model.User;
import com.tms.repository.UserRepository;
import com.tms.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock AuthenticationManager authenticationManager;
    @Mock org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    @Mock EmailService emailService;
    @Mock com.tms.config.AppProperties appProperties;
    @InjectMocks AuthService authService;

    @Test
    void register_shouldThrowWhenEmailExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        RegisterRequest req = new RegisterRequest("test@test.com", "Pass@1234", "Test", "User");
        assertThrows(DuplicateResourceException.class, () -> authService.register(req));
    }

    @Test
    void register_shouldSucceed() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString(), anyString());

        RegisterRequest req = new RegisterRequest("test@test.com", "Pass@1234", "Test", "User");
        var result = authService.register(req);
        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());
    }
}
