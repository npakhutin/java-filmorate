package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {
    private final UserStorage userStorage = new InMemoryUserStorage();
    private final FilmService service = new FilmService(new InMemoryFilmStorage(), userStorage);
    private Film film;
    private User user;

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
        user = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
    }

    @Test
    void create() {
        service.create(film);
        assertEquals(film, service.getById(film.getId()));
    }

    @Test
    void update() {
        service.create(film);
        film = Film.builder()
                .id(film.getId())
                .name("Updated Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
        service.update(film);
        assertEquals(film.getName(), service.getById(film.getId()).getName());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAll() {
        service.create(film);
        film = Film.builder()
                .name("Updated Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .build();
        service.create(film);
        assertEquals(2, service.getAll().size());
    }

    @Test
    void getById() {
        service.create(film);
        assertEquals(film, service.getById(film.getId()));
        assertThrows(UnknownFilmException.class,() -> service.getById(film.getId() + 1));
    }

    @Test
    void testSetLike() {
        service.create(film);
        userStorage.create(user);

        film = service.setLike(film.getId(), user.getId());
        assertEquals(1, film.getUsersLiked().size());
    }

    @Test
    void testDeleteLike() {
        service.create(film);
        userStorage.create(user);

        film = service.setLike(film.getId(), user.getId());
        assertEquals(1, film.getUsersLiked().size());

        film = service.deleteLike(film.getId(), user.getId());
        assertEquals(0, film.getUsersLiked().size());

        assertThrows(UnknownUserException.class, () -> service.deleteLike(film.getId(), 100));
    }

    @Test
    void testTopPopular() {
        for (int filmNum = 0; filmNum < 12; filmNum++) {
            film = Film.builder()
                    .name("Film Name" + filmNum)
                    .description("Film Description" + filmNum)
                    .releaseDate(LocalDate.of(1980, 12, 1))
                    .duration(180)
                    .build();
            film = service.create(film);
            for (int userId = 1; userId < film.getId(); userId++) {
                film.setLike(userId);
            }
        }
        List<Film> topFilms = service.getTopPopular(1);
        assertEquals(1, topFilms.size());
        assertEquals(12, topFilms.get(0).getId());

        topFilms = service.getTopPopular(10);
        for (int i = 0; i < topFilms.size(); i++) {
            assertEquals(12 - i, topFilms.get(i).getId());
        }
    }
}