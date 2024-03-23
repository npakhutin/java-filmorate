package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validation.Transfer;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    @Autowired
    private FilmService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Validated(Transfer.New.class) @RequestBody Film film) {
        log.info("POST /films");

        film = service.create(film);

        log.info("Film added with id {}", film.getId());
        return film;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@Validated(Transfer.Existing.class) @RequestBody Film film) {
        log.info("PUT /films");

        try {
            film = service.update(film);
        } catch (UnknownFilmException e) {
            log.warn(e.getMessage());
            throw e;
        }

        log.info("Film with id {} updated", film.getId());
        return film;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getAll() {
        log.info("GET /films");
        return service.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getById(@PathVariable Integer id) {
        log.info("GET /films/{}", id);
        return service.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film setLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("PUT /films/{}/like/{}", id, userId);

        try {
            Film film = service.setLike(id, userId);

            log.info("User with id {} liked film {}", userId, film.getId());
            return film;

        } catch (UnknownFilmException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("DELETE /films/{}/like/{}", id, userId);

        try {
            Film film = service.deleteLike(id, userId);

            log.info("User with id {} unliked film {}", userId, film.getId());
            return film;

        } catch (UnknownFilmException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getTopPopular(@RequestParam(required = false, defaultValue = "10") Integer count) {
        log.info("GET /films/popular?count={}", count);

        return service.getTopPopular(count);
    }

}


