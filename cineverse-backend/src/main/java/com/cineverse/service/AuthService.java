package com.cineverse.service;

import com.cineverse.dto.AuthResponse;
import com.cineverse.dto.LoginRequest;
import com.cineverse.dto.RegisterRequest;
import com.cineverse.dto.UserDto;
import com.cineverse.exception.ConflictException;
import com.cineverse.model.Role;
import com.cineverse.model.User;
import com.cineverse.repository.UserRepository;
import com.cineverse.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
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
        user.setDob(req.getDob() != null && !req.getDob().isBlank() ? LocalDate.parse(req.getDob()) : null);
        user.setGender(req.getGender() != null ? req.getGender() : "");
        user.setAvatar(avatarFromName(req.getName()));
        user.setJoinedOn(LocalDate.now());
        user.setRole(Role.ROLE_CUSTOMER);

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserDto.from(user));
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new com.cineverse.exception.BadRequestException("Invalid credentials"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, UserDto.from(user));
    }

    public UserDto loginUserOnly(LoginRequest req) {
        return login(req).getUser();
    }

    private String avatarFromName(String name) {
        String[] parts = name.trim().split("\\s+");
        return Arrays.stream(parts)
                .map(p -> p.substring(0, 1))
                .collect(Collectors.joining())
                .toUpperCase()
                .substring(0, Math.min(2, parts.length));
    }
}
