package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (filmStorage.getById(film.getId()) == null) {
            throw new UnknownFilmException("В хранилище не найден фильм для обновления с id = " + film.getId());
        }
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Integer id) {
        Film film = filmStorage.getById(id);
        if (film == null) {
            throw new UnknownFilmException("В хранилище не найден фильм с id = " + id);
        }
        return film;
    }

    public Film setLike(Integer id, Integer userId) {
        Film film = getById(id);
        checkUserInStorage(userId);
        film.setLike(userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) {
        Film film = getById(id);
        checkUserInStorage(userId);
        film.deleteLike(userId);
        return film;
    }

    public List<Film> getTopPopular(Integer count) {
        return filmStorage.getAll().stream()
                .sorted(Comparator.comparingInt((Film o) -> o.getUsersLiked().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void checkUserInStorage(Integer userId) {
        if (userStorage.getById(userId) == null) {
            throw new UnknownUserException("В хранилище не найден пользователь с id = " + userId);
        }
    }
}
