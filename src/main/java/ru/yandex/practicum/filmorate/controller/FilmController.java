package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Integer filmIdCounter = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Validated(Transfer.New.class) @RequestBody Film film) {
        log.info("POST /films");

        film.setId(getNextFilmId());
        films.put(film.getId(), film);

        log.info("Film added with id {}", film.getId());
        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Validated(Transfer.Existing.class) @RequestBody Film film) {
        log.info("PUT /films");

        if (!films.containsKey(film.getId())) {
            var m = "Unknown film with id = " + film.getId();
            log.warn(m);
            throw new UnknownFilmException(m);
        }
        films.put(film.getId(), film);

        log.info("Film with id {} updated", film.getId());
        return film;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAll() {
        log.info("GET /films");
        return List.copyOf(films.values());
    }

    private int getNextFilmId() {
        return ++filmIdCounter;
    }

}


