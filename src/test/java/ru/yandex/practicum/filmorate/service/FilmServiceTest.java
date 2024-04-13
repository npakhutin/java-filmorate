package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.UnknownFilmException;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmServiceTest {
    private final UserStorage userStorage;
    private final FilmService service;
    private Film film;
    private User user;

    @Autowired
    public FilmServiceTest(UserStorage userStorage, FilmService service) {
        this.userStorage = userStorage;
        this.service = service;
    }

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
        assertThrows(UnknownFilmException.class, () -> service.getById(film.getId() + 1));
    }

    @Test
    void testAddLike() {
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
        for (int i = 0; i < 12; i++) {
            film = Film.builder()
                    .name("Film Name" + i)
                    .description("Film Description" + i)
                    .releaseDate(LocalDate.of(1980, 12, 1))
                    .duration(180)
                    .build();
            film = service.create(film);

            user = User.builder()
                    .login("user_login" + i)
                    .name("User Name" + i)
                    .email("user" + i + "@mail.ru")
                    .birthday(LocalDate.of(1980, 12, 1))
                    .build();
            user = userStorage.create(user);

            for (int userId = 1; userId < film.getId(); userId++) {

                service.setLike(film.getId(), userId);
                //film.setLike(userId);
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