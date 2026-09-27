package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.service.ChangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/changes")
public class ChangeController {

    private final ChangeService changeService;

    public ChangeController(ChangeService changeService) {
        this.changeService = changeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String bookingId,
            @RequestParam(required = false) String changeType
    ) {
        List<Map<String, Object>> rows = changeService.findAll(customerId, bookingId, changeType);
        return ResponseEntity.ok(ApiResponse.ok(rows, rows.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(changeService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    @SuppressWarnings("unchecked")
    public ResponseEntity<ApiResponse<Map<String, Object>>> applyChange(@RequestBody Map<String, Object> body) {
        String bookingId = String.valueOf(body.get("bookingId"));
        Map<String, Object> updates = (Map<String, Object>) body.get("updates");
        String changeType = String.valueOf(body.get("changeType"));
        String description = body.get("description") != null ? String.valueOf(body.get("description")) : "";

        Map<String, Object> data = changeService.applyChange(bookingId, updates, changeType, description);
        ApiResponse<Map<String, Object>> response = ApiResponse.created(data, null);
        response.setMessage("Booking updated! A change fee of ₹50 has been applied.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
