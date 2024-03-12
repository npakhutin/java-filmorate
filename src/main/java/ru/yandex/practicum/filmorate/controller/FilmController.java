package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class FilmController {
    private Integer filmIdCounter = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping(value = "/films")
    public ResponseEntity<Film> create(@Validated(Transfer.New.class) @RequestBody Film film) {
        log.info("POST /films");

        film.setId(getNextFilmId());
        films.put(film.getId(), film);

        log.info("Film added with id {}", film.getId());
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }

    @PutMapping("/films")
    public ResponseEntity<Film> update(@Validated(Transfer.Existing.class) @RequestBody Film film) {
        log.info("PUT /films");

        if (!films.containsKey(film.getId())) {
            var m = "Unknown film with id = " + film.getId();
            log.warn(m);
            throw new UnknownFilmException(m);
        }
        films.put(film.getId(), film);

        log.info("Film with id {} updated", film.getId());
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/films")
    public ResponseEntity<List<Film>> getAll() {
        log.info("GET /films");
        return new ResponseEntity<>(List.copyOf(films.values()), HttpStatus.OK);
    }

    private int getNextFilmId() {
        return ++filmIdCounter;
    }

}
