package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validation.Transfer;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Validated(Transfer.New.class) @RequestBody Film film) {
        log.info("POST /films");
        film = service.create(film);
        log.info("Добавлен фильм с id {}", film.getId());

        return film;
    }

    @PutMapping
    public Film update(@Validated(Transfer.Existing.class) @RequestBody Film film) {
        log.info("PUT /films");
        film = service.update(film);
        log.info("Обновлен фильм с id {}", film.getId());

        return film;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("GET /films");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film getById(@PathVariable Integer id) {
        log.info("GET /films/{}", id);
        return service.getById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film setLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("PUT /films/{}/like/{}", id, userId);
        Film film = service.setLike(id, userId);
        log.info("Пользователь с id {} поставил лайк фильму {}", userId, film.getId());

        return film;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.info("DELETE /films/{}/like/{}", id, userId);
        Film film = service.deleteLike(id, userId);
        log.info("Пользователь с id {} удалил лайк фильму {}", userId, film.getId());

        return film;
    }

    @GetMapping("/popular")
    public List<Film> getTopPopular(@RequestParam(defaultValue = "10") @Positive Integer count) {
        log.info("GET /films/popular?count={}", count);

        return service.getTopPopular(count);
    }
}


