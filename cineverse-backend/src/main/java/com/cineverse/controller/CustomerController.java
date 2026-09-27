package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.dto.AuthResponse;
import com.cineverse.dto.LoginRequest;
import com.cineverse.dto.RegisterRequest;
import com.cineverse.dto.UserDto;
import com.cineverse.service.AuthService;
import com.cineverse.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final UserService userService;
    private final AuthService authService;

    public CustomerController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDto>>> getAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(ApiResponse.ok(users, users.size()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        ApiResponse<AuthResponse> body = ApiResponse.ok(response);
        body.setMessage("Login successful");
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response, "Account created!"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(userService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        UserDto user = userService.update(id, body);
        ApiResponse<UserDto> response = ApiResponse.ok(user);
        response.setMessage("Profile updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Customer deleted"));
    }
}
