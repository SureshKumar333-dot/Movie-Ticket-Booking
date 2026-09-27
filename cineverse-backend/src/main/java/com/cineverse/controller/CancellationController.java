package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.service.CancellationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cancellations")
public class CancellationController {

    private final CancellationService cancellationService;

    public CancellationController(CancellationService cancellationService) {
        this.cancellationService = cancellationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String bookingId
    ) {
        List<Map<String, Object>> rows = cancellationService.findAll(customerId, bookingId);
        return ResponseEntity.ok(ApiResponse.ok(rows, rows.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(cancellationService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancel(@RequestBody Map<String, Object> body) {
        String bookingId = String.valueOf(body.get("bookingId"));
        String reason = body.get("reason") != null ? String.valueOf(body.get("reason")) : "";
        Map<String, Object> data = cancellationService.cancelBooking(bookingId, reason);
        ApiResponse<Map<String, Object>> response = ApiResponse.created(data, null);
        response.setMessage("Booking cancelled. Refund will be processed within 5–7 days.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(cancellationService.updateStatus(id, body)));
    }
}
