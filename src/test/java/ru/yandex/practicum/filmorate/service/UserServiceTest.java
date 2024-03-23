package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exceptions.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(UserService.class)
class UserServiceTest {
    @Autowired
    private UserService service;
    User user;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserStorage userStorage() {
            return new InMemoryUserStorage();
        }
    }

    @BeforeEach
    void setUp() {
        user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
    }

    @Test
    void create() {
        service.create(user);
        assertEquals(user, service.getById(user.getId()));
    }

    @Test
    void update() {
        service.create(user);
        user = new User(user.getId(), "user2_login", "User2 Name", "user2@mail.ru", LocalDate.of(1980, 12, 1));
        service.update(user);
        assertEquals(user.getName(), service.getById(user.getId()).getName());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAll() {
        service.create(user);
        user = new User(null, "user2_login", "User2 Name", "user2@mail.ru", LocalDate.of(1980, 12, 1));
        service.create(user);
        assertEquals(2, service.getAll().size());
    }

    @Test
    void getById() {
        service.create(user);
        assertEquals(user, service.getById(user.getId()));
        assertThrows(UnknownUserException.class, () -> service.getById(user.getId() + 1));
        assertThrows(UnknownUserException.class, () -> service.getById(null));
    }

    @Test
    void testGetAddDeleteFriend() {
        User friend = new User(null, "friend_login", "Friend Name", "friend@mail.ru", LocalDate.of(1980, 12, 1));

        user = service.create(user);
        friend = service.create(friend);

        user = service.addFriend(user.getId(), friend.getId());
        assertEquals(List.of(friend.getId()), user.getFriendIds());
        assertEquals(List.of(user.getId()), friend.getFriendIds());

        user = service.deleteFriend(user.getId(), friend.getId());
        assertEquals(0, user.getFriendIds().size());
        assertEquals(0, friend.getFriendIds().size());
    }
}