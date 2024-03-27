package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class UserStorageTest<T extends UserStorage> {
    private User user;
    protected T storage;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
    }

    @Test
    void testCreate() {
        User actualUser = storage.create(user);
        assertNotNull(actualUser.getId());
        assertEquals(user.getName(), actualUser.getName());
    }

    @Test
    void updateOk() {
        user = storage.create(user);

        user.setName("New Name");
        User actualUser = storage.update(user);
        assertEquals(user, actualUser);
    }

    @Test
    void updateWrongId() {
        storage.create(user);

        User unknownUser = User.builder()
                .id(2)
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        assertThrows(UnknownUserException.class, () -> storage.update(unknownUser));
    }

    @Test
    void testGetAll() {
        storage.create(user);
        User user2 = User.builder()
                .login("user2_login")
                .name("User2 Name")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        storage.create(user2);
        assertEquals(2, storage.getAll().size());
    }

    @Test
    void testGetById() {
        storage.create(user);
        assertEquals(user, storage.getById(user.getId()));
    }
}