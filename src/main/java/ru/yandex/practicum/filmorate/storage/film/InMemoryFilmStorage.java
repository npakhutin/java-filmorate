package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Integer filmIdCounter = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(getNextFilmId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new UnknownFilmException("В хранилище не найден фильм с id = " + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return List.copyOf(films.values());
    }

    @Override
    public Film getById(Integer id) {
        if (!films.containsKey(id)) {
            throw new UnknownFilmException("В хранилище не найден фильм с id = " + id);
        }
        return films.get(id);
    }

    private int getNextFilmId() {
        return ++filmIdCounter;
    }
}
