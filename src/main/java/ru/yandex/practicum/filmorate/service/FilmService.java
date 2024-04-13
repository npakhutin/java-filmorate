package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film)
                .orElseThrow(() -> new UnknownFilmException("В хранилище не найден фильм для обновления с id = " + film.getId()));
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Integer id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new UnknownFilmException("В хранилище не найден фильм с id = " + id));
    }

    public Film setLike(Integer id, Integer userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new UnknownUserException("В хранилище не найден пользователь с id = " + userId + " для постановки лайка");
        }
        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new UnknownFilmException("В хранилище не найден фильм для лайка с id = " + id));
        film.addLike(userId);
        filmStorage.saveLike(id, userId);
        return film;
    }

    public Film deleteLike(Integer id, Integer userId) {
        if (userStorage.getById(userId).isEmpty()) {
            throw new UnknownUserException("В хранилище не найден пользователь с id = " + userId + " для удаления лайка");
        }

        Film film = filmStorage.getById(id)
                .orElseThrow(() -> new UnknownFilmException("В хранилище не найден фильм для удаления лайка с id = " + id));
        film.deleteLike(userId);
        filmStorage.deleteLike(id, userId);
        return film;
    }

    public List<Film> getTopPopular(Integer count) {
        return filmStorage.getAll()
                .stream()
                .sorted(Comparator.comparingInt((Film o) -> o.getUsersLiked().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
