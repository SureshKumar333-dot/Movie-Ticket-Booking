package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status
    ) {
        List<Map<String, Object>> bookings = bookingService.findAll(customerId, status);
        return ResponseEntity.ok(ApiResponse.ok(bookings, bookings.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> booking = bookingService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(booking, "Booking confirmed! 🎬"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Map<String, Object> booking = bookingService.update(id, body);
        ApiResponse<Map<String, Object>> response = ApiResponse.ok(booking);
        response.setMessage("Booking updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable String id) {
        bookingService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Booking deleted"));
    }
}
