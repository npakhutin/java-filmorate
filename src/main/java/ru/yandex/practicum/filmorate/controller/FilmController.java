package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class FilmController {
    private Integer filmIdCounter = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping("/films")
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST /films");
        if (film.getId() != null) {
            log.warn("Error: new film id should be null, actual id = " + film.getId());
            throw new FilmAlreadyExistsException();
        }
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        log.info("Film added with id {}", film.getId());
        return film;
    }

    @PutMapping("/films")
    public Film update(@Valid @RequestBody Film film) {
        log.info("PUT /films");
        if (film.getId() == null || !films.containsKey(film.getId())) {
            log.warn("Film id == null or id == " + film.getId() + " is not in films");
            throw new UnknownFilmException();
        }
        films.put(film.getId(), film);
        log.info("Film with id {} updated", film.getId());
        return film;
    }

    @GetMapping("/films")
    public Collection<Film> getAll() {
        log.info("GET /films");
        return films.values();
    }

    private int getNextFilmId() {
        return ++filmIdCounter;
    }

}
