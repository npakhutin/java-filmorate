package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql({"classpath:schema.sql", "classpath:data.sql"})
class DictionaryDbStorageTest {
    private final DictionaryDbStorage storage;
    private final FilmStorage filmStorage;

    @Autowired
    DictionaryDbStorageTest(DictionaryDbStorage storage, FilmStorage filmStorage) {
        this.storage = storage;
        this.filmStorage = filmStorage;
    }

    @Test
    void getAllGenres() {
        List<Genre> genres = storage.getAllGenres();
        assertFalse(genres.isEmpty());
    }

    @Test
    void getGenreById() {
        List<Genre> genres = storage.getAllGenres();
        Genre genre = storage.getGenreById(genres.get(0).getId()).orElse(null);
        assertNotNull(genre);
        assertEquals(genres.get(0), genre);

        assertTrue(storage.getGenreById(-1).isEmpty());
    }

    @Test
    void getGenresByFilmId() {
        Film film = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
        film.addGenre(storage.getGenreById(1).orElseThrow(() -> new RuntimeException("Не найден жанр")));
        film.addGenre(storage.getGenreById(2).orElseThrow(() -> new RuntimeException("Не найден жанр")));
        film = filmStorage.create(film);

        List<Genre> genres = storage.getGenresByFilmId(film.getId());
        assertFalse(genres.isEmpty());
    }

    @Test
    void getMpaById() {
        List<MpaRating> ratings = storage.getAllMpa();
        assertFalse(ratings.isEmpty());
        MpaRating rating = storage.getMpaById(ratings.get(0).getId()).orElse(null);
        assertNotNull(rating);
        assertEquals(rating, ratings.get(0));
    }

    @Test
    void getAllMpa() {
        List<MpaRating> ratings = storage.getAllMpa();
        assertFalse(ratings.isEmpty());
    }
}