package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public abstract class UserStorageTest<T extends UserStorage> {

    protected T storage;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCreate() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        User actualUser = storage.create(user);
        assertNotNull(actualUser.getId());
        assertEquals(user.getName(), actualUser.getName());
    }

    @Test
    void updateOk() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        user = storage.create(user);

        user.setName("New Name");
        User actualUser = storage.update(user);
        assertEquals(user, actualUser);
    }

    @Test
    void updateWrongId() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        storage.create(user);

        User unknownUser = new User(2, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        assertThrows(UnknownUserException.class, () -> storage.update(unknownUser));
    }

    @Test
    void testGetAll() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        storage.create(user);
        user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        storage.create(user);
        assertEquals(2, storage.getAll().size());
    }

    @Test
    void testGetById() {
        User user = storage.create(new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1)));
        assertEquals(user, storage.getById(user.getId()));
        assertThrows(UnknownUserException.class, () -> storage.getById(user.getId() + 1));
        assertThrows(UnknownUserException.class, () -> storage.getById(null));
    }
}