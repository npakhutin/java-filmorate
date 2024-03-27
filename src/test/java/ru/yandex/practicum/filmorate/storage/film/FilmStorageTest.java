package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class FilmStorageTest<T extends FilmStorage> {
    private Film film;
    protected T storage;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
    }

    @Test
    void create() {

        Film actualFilm = storage.create(film);
        assertNotNull(actualFilm.getId());
        assertEquals(film.getName(), actualFilm.getName());
    }

    @Test
    void updateOk() {
        film = storage.create(film);

        film.setName("New Name");
        Film actualFilm = storage.update(film);
        assertEquals(film, actualFilm);
    }

    @Test
    void updateWrongId() {
        storage.create(film);
        Film film1 = Film.builder()
                .id(2)
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();

        assertThrows(UnknownFilmException.class, () -> storage.update(film1));
    }

    @Test
    void getAll() {
        storage.create(film);

        Film film1 = Film.builder()
                .name("Film1 Name")
                .description("Film1 Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
        storage.create(film1);
        assertEquals(2, storage.getAll().size());
    }

    @Test
    void getById() {
        Film film1 = Film.builder()
                .name("Updated Name")
                .description("Film1 Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
        storage.create(film);
        storage.create(film1);
        assertEquals(film, storage.getById(film.getId()));
        assertEquals(film1, storage.getById(film1.getId()));
    }
}