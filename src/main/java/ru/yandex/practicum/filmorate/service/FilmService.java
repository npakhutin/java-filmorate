package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    @Autowired
    private FilmStorage storage;

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public List<Film> getAll() {
        return storage.getAll();
    }

    public Film getById(Integer id) {
        return storage.getById(id);
    }

    public Film setLike(Integer id, Integer userId) {
        Film film = storage.getById(id);
        film.setLike(userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) {
        Film film = storage.getById(id);
        film.deleteLike(userId);
        return film;
    }

    public List<Film> getTopPopular(Integer count) {
        if (count == null || count <= 0) {
            throw new IllegalArgumentException("Count should be positive");
        }

        return storage.getAll().stream()
                .sorted(Comparator.comparingInt((Film o) -> o.getUsersLiked().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
