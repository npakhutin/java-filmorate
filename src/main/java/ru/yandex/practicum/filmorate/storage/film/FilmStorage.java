package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Optional<Film> update(Film film);

    List<Film> getAll();

    Optional<Film> getById(int id);

    void saveLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getTopPopular(Integer count);
}
