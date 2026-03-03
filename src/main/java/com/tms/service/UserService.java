package com.tms.service;

import com.tms.dto.request.UpdatePasswordRequest;
import com.tms.dto.request.UpdateProfileRequest;
import com.tms.dto.response.PagedResponse;
import com.tms.dto.response.UserResponse;
import com.tms.exception.BadRequestException;
import com.tms.exception.ResourceNotFoundException;
import com.tms.model.User;
import com.tms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Cacheable(value = "userProfile", key = "#email")
    public UserResponse getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }

    @CacheEvict(value = "userProfile", key = "#email")
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = getUserEntityByEmail(email);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        return UserResponse.from(userRepository.save(user));
    }

    public void updatePassword(String email, UpdatePasswordRequest request) {
        User user = getUserEntityByEmail(email);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public PagedResponse<UserResponse> listAllUsers(Pageable pageable) {
        return PagedResponse.from(
                userRepository.findAllByOrderByCreatedAtDesc(pageable).map(UserResponse::from));
    }

    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        user.setActive(false);
        userRepository.save(user);
    }
}
