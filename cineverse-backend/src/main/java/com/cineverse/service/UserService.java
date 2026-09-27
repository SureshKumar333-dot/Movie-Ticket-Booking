package com.cineverse.service;

import com.cineverse.dto.RegisterRequest;
import com.cineverse.dto.UserDto;
import com.cineverse.exception.BadRequestException;
import com.cineverse.exception.ConflictException;
import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Role;
import com.cineverse.model.User;
import com.cineverse.repository.UserRepository;
import com.cineverse.util.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserDto::from).toList();
    }

    public UserDto findById(String id) {
        User current = SecurityUtil.currentUser();
        if (!SecurityUtil.isAdmin(current) && !current.getId().equals(id)) {
            throw new BadRequestException("You can only view your own profile");
        }
        return userRepository.findById(id)
                .map(UserDto::from)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    }

    @Transactional
    public UserDto register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered");
        }
        User user = new User();
        user.setId("C" + String.valueOf(System.currentTimeMillis()).substring(7));
        user.setName(req.getName().trim());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setPhone(req.getPhone());
        user.setCity(req.getCity());
        user.setAddress(req.getAddress() != null ? req.getAddress() : "");
        if (req.getDob() != null && !req.getDob().isBlank()) {
            user.setDob(LocalDate.parse(req.getDob()));
        }
        user.setGender(req.getGender() != null ? req.getGender() : "");
        user.setAvatar(req.getName().trim().substring(0, 1).toUpperCase());
        user.setJoinedOn(LocalDate.now());
        user.setRole(Role.ROLE_CUSTOMER);
        userRepository.save(user);
        return UserDto.from(user);
    }

    @Transactional
    public UserDto update(String id, Map<String, Object> updates) {
        User current = SecurityUtil.currentUser();
        if (!SecurityUtil.isAdmin(current) && !current.getId().equals(id)) {
            throw new BadRequestException("You can only update your own profile");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (updates.containsKey("name")) user.setName(String.valueOf(updates.get("name")));
        if (updates.containsKey("phone")) user.setPhone(String.valueOf(updates.get("phone")));
        if (updates.containsKey("city")) user.setCity(String.valueOf(updates.get("city")));
        if (updates.containsKey("address")) user.setAddress(String.valueOf(updates.get("address")));
        if (updates.containsKey("gender")) user.setGender(String.valueOf(updates.get("gender")));
        if (updates.containsKey("dob") && updates.get("dob") != null && !String.valueOf(updates.get("dob")).isBlank()) {
            user.setDob(LocalDate.parse(String.valueOf(updates.get("dob"))));
        }
        if (updates.containsKey("password")) {
            String pwd = String.valueOf(updates.get("password"));
            if (pwd.length() >= 6) user.setPassword(passwordEncoder.encode(pwd));
        }

        return UserDto.from(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        userRepository.deleteById(id);
    }
}
