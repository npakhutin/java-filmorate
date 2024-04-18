package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DictionaryDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class FilmStorageTest<T extends FilmStorage> {
    protected T storage;
    protected UserStorage userStorage;
    protected DictionaryDbStorage dictionaryStorage;

    private Film film;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .mpa(MpaRating.builder().id(1).name("G").build())
                .build();
        film.addGenre(dictionaryStorage.getGenreById(1).orElseThrow());
    }

    @Test
    void create() {
        Film actualFilm = storage.create(film);
        assertNotNull(actualFilm.getId());
        assertEquals(film.getName(), actualFilm.getName());
        assertFalse(actualFilm.getGenres().isEmpty());
        //проверка создания с непустым id
        assertThrows(NullPointerException.class, () -> storage.create(actualFilm));
    }

    @Test
    void updateOk() {
        film = storage.create(film);

        film.setName("New Name");
        Optional<Film> optionalFilm = storage.update(film);
        assertEquals(film, optionalFilm.orElse(null));
    }

    @Test
    void updateWrongId() {
        storage.create(film);
        Film film1 = Film.builder()
                .id(-1)
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .mpa(MpaRating.builder().id(1).build())
                .build();

        Optional<Film> optionalFilm = storage.update(film1);
        assertTrue(optionalFilm.isEmpty());
    }

    @Test
    void getAll() {
        storage.create(film);

        Film film1 = Film.builder()
                .name("Film1 Name")
                .description("Film1 Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .mpa(MpaRating.builder().id(1).build())
                .build();
        storage.create(film1);
        assertNotEquals(0, storage.getAll().size());
    }

    @Test
    void getById() {
        storage.create(film);

        Optional<Film> optionalFilm = storage.getById(film.getId());
        assertEquals(film, optionalFilm.orElse(null));

        optionalFilm = storage.getById(-100);
        assertTrue(optionalFilm.isEmpty());
    }
}