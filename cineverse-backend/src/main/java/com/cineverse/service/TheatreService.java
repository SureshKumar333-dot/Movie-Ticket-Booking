package com.cineverse.service;

import com.cineverse.exception.ResourceNotFoundException;
import com.cineverse.model.Hall;
import com.cineverse.model.Theatre;
import com.cineverse.repository.HallRepository;
import com.cineverse.repository.TheatreRepository;
import com.cineverse.util.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final HallRepository hallRepository;

    public TheatreService(TheatreRepository theatreRepository, HallRepository hallRepository) {
        this.theatreRepository = theatreRepository;
        this.hallRepository = hallRepository;
    }

    public List<Map<String, Object>> findAll(String city) {
        List<Theatre> theatres = (city != null && !city.isBlank())
                ? theatreRepository.findByCity(city)
                : theatreRepository.findAll();
        return theatres.stream()
                .map(t -> Mappers.theatreToMap(t, hallRepository.findByTheatreId(t.getId())))
                .toList();
    }

    public Map<String, Object> findById(String id) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));
        return Mappers.theatreToMap(theatre, hallRepository.findByTheatreId(id));
    }

    @Transactional
    public Map<String, Object> create(Map<String, Object> body) {
        Theatre theatre = new Theatre();
        theatre.setId("T" + String.valueOf(System.currentTimeMillis()).substring(7));
        applyBody(theatre, body);
        theatreRepository.save(theatre);
        return Mappers.theatreToMap(theatre, List.of());
    }

    @Transactional
    public Map<String, Object> update(String id, Map<String, Object> body) {
        Theatre theatre = theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found"));
        applyBody(theatre, body);
        theatreRepository.save(theatre);
        return Mappers.theatreToMap(theatre, hallRepository.findByTheatreId(id));
    }

    @Transactional
    public void delete(String id) {
        if (!theatreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Theatre not found");
        }
        theatreRepository.deleteById(id);
    }

    private void applyBody(Theatre theatre, Map<String, Object> body) {
        if (body.containsKey("name")) theatre.setName(String.valueOf(body.get("name")));
        if (body.containsKey("city")) theatre.setCity(String.valueOf(body.get("city")));
        if (body.containsKey("location")) theatre.setLocation(String.valueOf(body.get("location")));
        if (body.containsKey("phone")) theatre.setPhone(String.valueOf(body.get("phone")));
    }
}
