package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Optional<Film> update(Film film);

    List<Film> getAll();

    Optional<Film> getById(Integer id);

    void saveLike(Integer filmId, Integer userId);

    void deleteLike(Integer filmId, Integer userId);
}
