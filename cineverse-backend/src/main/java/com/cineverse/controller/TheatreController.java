package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.service.TheatreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(@RequestParam(required = false) String city) {
        List<Map<String, Object>> theatres = theatreService.findAll(city);
        return ResponseEntity.ok(ApiResponse.ok(theatres, theatres.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> theatre = theatreService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(theatre, "Theatre created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.update(id, body)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable String id) {
        theatreService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Theatre deleted"));
    }
}
