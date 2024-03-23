package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public abstract class FilmStorageTest<T extends FilmStorage> {

    protected T storage;

    @BeforeEach
    void setUp() {

    }

    @Test
    void create() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        Film actualFilm = storage.create(film);
        assertNotNull(actualFilm.getId());
        assertEquals(film.getName(), actualFilm.getName());
    }

    @Test
    void updateOk() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film = storage.create(film);

        film.setName("New Name");
        Film actualFilm = storage.update(film);
        assertEquals(film, actualFilm);
    }

    @Test
    void updateWrongId() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        storage.create(film);

        Film film1 = new Film(2, "Film1 Name", "Film1 Description", LocalDate.of(1980, 12, 1), 180);
        assertThrows(UnknownFilmException.class, () -> storage.update(film1));
    }

    @Test
    void getAll() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        storage.create(film);

        film = new Film(null, "Film2 Name", "Film2 Description", LocalDate.of(1980, 12, 1), 180);
        storage.create(film);
        assertEquals(2, storage.getAll().size());
    }

    @Test
    void getById() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        storage.create(film);
        Film film1 = new Film(null, "Updated Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        storage.create(film1);
        assertEquals(film, storage.getById(film.getId()));
        assertEquals(film1, storage.getById(film1.getId()));
    }
}