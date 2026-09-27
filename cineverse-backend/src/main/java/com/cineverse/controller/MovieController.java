package com.cineverse.controller;

import com.cineverse.dto.ApiResponse;
import com.cineverse.service.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAll(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String title
    ) {
        List<Map<String, Object>> movies = movieService.findAll(genre, language, title);
        return ResponseEntity.ok(ApiResponse.ok(movies, movies.size()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(movieService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> movie = movieService.create(body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(movie, "Movie created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(@PathVariable String id, @RequestBody Map<String, Object> body) {
        Map<String, Object> movie = movieService.update(id, body);
        ApiResponse<Map<String, Object>> response = ApiResponse.ok(movie);
        response.setMessage("Movie updated");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> delete(@PathVariable String id) {
        movieService.delete(id);
        return ResponseEntity.ok(ApiResponse.message("Movie deleted"));
    }
}
