package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.service.ShowService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String movieId,
            @RequestParam(required = false) String hallId,
            @RequestParam(required = false) String theatreId,
            @RequestParam(required = false) String date
    ) {
        List<Map<String, Object>> shows = showService.findAll(movieId, hallId, theatreId, date);
        return ResponseEntity.ok(ApiResponse.ok(shows, shows.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(showService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> show = showService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(show, "Show created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(showService.update(id, body)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable String id) {
        showService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Show deleted"));
    }
}
