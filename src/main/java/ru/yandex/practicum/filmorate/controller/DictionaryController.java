package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.DictionaryService;

import java.util.List;

@Slf4j
@RestController
public class DictionaryController {
    private final DictionaryService service;

    @Autowired
    public DictionaryController(DictionaryService service) {
        this.service = service;
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        log.info("GET /genres");
        return service.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        log.info("GET /genres/{}", id);
        return service.getGenreById(id);
    }

    @GetMapping("/mpa")
    public List<MpaRating> getAllMpa() {
        log.info("GET /mpa");
        return service.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaById(@PathVariable Integer id) {
        log.info("GET /mpa/{}", id);
        return service.getMpaById(id);
    }
}
