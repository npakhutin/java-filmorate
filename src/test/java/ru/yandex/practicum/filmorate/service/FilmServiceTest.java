package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.UnknownFilmException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(FilmService.class)
class FilmServiceTest {
    @Autowired
    private FilmService service;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FilmStorage filmStorage() {
            return new InMemoryFilmStorage();
        }
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void create() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);
        assertEquals(film, service.getById(film.getId()));
    }

    @Test
    void update() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);
        film = new Film(film.getId(), "Updated Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.update(film);
        assertEquals(film.getName(), service.getById(film.getId()).getName());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAll() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);
        film = new Film(null, "Updated Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);
        assertEquals(2, service.getAll().size());
    }

    @Test
    void getById() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);
        assertEquals(film, service.getById(film.getId()));
        assertThrows(UnknownFilmException.class, () -> service.getById(film.getId() + 1));
        assertThrows(UnknownFilmException.class, () -> service.getById(null));
    }

    @Test
    void testSetLike() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);

        film = service.setLike(film.getId(), 1);
        assertEquals(1, film.getUsersLiked().size());
    }

    @Test
    void testDeleteLike() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        service.create(film);

        film = service.setLike(film.getId(), 1);
        assertEquals(1, film.getUsersLiked().size());

        film = service.deleteLike(film.getId(), 1);
        assertEquals(0, film.getUsersLiked().size());
    }

    @Test
    void testTopPopular() {
        for (int filmNum = 0; filmNum < 12; filmNum++) {
            Film film = new Film(null, "Film Name" + filmNum, "Film Description" + filmNum, LocalDate.of(1980, 12, 1), 180);
            film = service.create(film);
            for (int userId = 1; userId < film.getId(); userId++) {
                film.setLike(userId);
            }
        }
        assertThrows(IllegalArgumentException.class, () -> service.getTopPopular(null));
        assertThrows(IllegalArgumentException.class, () -> service.getTopPopular(0));
        List<Film> topFilms = service.getTopPopular(1);
        assertEquals(1, topFilms.size());
        assertEquals(12, topFilms.get(0).getId());

        topFilms = service.getTopPopular(10);
        for (int i = 0; i < topFilms.size(); i++) {
            assertEquals(12 - i, topFilms.get(i).getId());
        }
    }
}