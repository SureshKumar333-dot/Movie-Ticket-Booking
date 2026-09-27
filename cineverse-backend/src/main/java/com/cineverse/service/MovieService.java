package com.cineverse.service;

import com.cineverse.exception.ConflictException;
import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Movie;
import com.cineverse.repository.MovieRepository;
import com.cineverse.util.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Map<String, Object>> findAll(String genre, String language, String title) {
        return movieRepository.findAll().stream()
                .filter(m -> genre == null || genre.isBlank() || "All Genres".equals(genre) || m.getGenre().contains(genre))
                .filter(m -> language == null || language.isBlank() || "All Languages".equals(language) || language.equals(m.getLanguage()))
                .filter(m -> title == null || title.isBlank() || m.getTitle().toLowerCase().contains(title.toLowerCase()))
                .map(Mappers::movieToMap)
                .toList();
    }

    public Map<String, Object> findById(String id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        return Mappers.movieToMap(movie);
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body) {
        String title = String.valueOf(body.get("title"));
        movieRepository.findByTitleIgnoreCase(title).ifPresent(m -> {
            throw new ConflictException("Movie already exists");
        });

        Movie movie = new Movie();
        movie.setId("M" + String.valueOf(System.currentTimeMillis()).substring(7));
        applyBody(movie, body);
        return Mappers.movieToMap(movieRepository.save(movie));
    }

    @Transactional
    public Map<String, Object> update(String id, Map<String, Object> body) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        applyBody(movie, body);
        return Mappers.movieToMap(movieRepository.save(movie));
    }

    @Transactional
    public void delete(String id) {
        if (!movieRepository.existsById(id)) {
            throw new ResourceNotFoundException("Movie not found");
        }
        movieRepository.deleteById(id);
    }

    @SuppressWarnings("unchecked")
    private void applyBody(Movie movie, Map<String, Object> body) {
        if (body.containsKey("title")) movie.setTitle(String.valueOf(body.get("title")).trim());
        if (body.containsKey("language")) movie.setLanguage(String.valueOf(body.get("language")));
        if (body.containsKey("genre")) movie.setGenre(String.valueOf(body.get("genre")));
        if (body.containsKey("duration")) movie.setDuration(String.valueOf(body.get("duration")));
        if (body.containsKey("rating")) movie.setRating(String.valueOf(body.get("rating")));
        if (body.containsKey("badge")) movie.setBadge(String.valueOf(body.get("badge")));
        if (body.containsKey("description")) movie.setDescription(String.valueOf(body.get("description")));
        if (body.containsKey("cast")) movie.setCastMembers(String.valueOf(body.get("cast")));
        if (body.containsKey("director")) movie.setDirector(String.valueOf(body.get("director")));
        if (body.containsKey("poster")) movie.setPoster(String.valueOf(body.get("poster")));
        if (body.get("price") instanceof Map<?, ?> price) {
            if (price.get("standard") != null) movie.setPriceStandard(((Number) price.get("standard")).intValue());
            if (price.get("premium") != null) movie.setPricePremium(((Number) price.get("premium")).intValue());
        }
    }
}
