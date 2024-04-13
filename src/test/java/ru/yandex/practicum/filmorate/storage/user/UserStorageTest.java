package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class UserStorageTest<T extends UserStorage> {
    protected T storage;
    private User user;
    private User friend;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

        friend = User.builder()
                .login("friend_login")
                .name("Friend Name")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
    }

    @Test
    void testCreate() {
        friend = storage.create(friend);
        user.addFriend(friend);
        User actualUser = storage.create(user);
        assertNotNull(actualUser.getId());
        assertEquals(user.getName(), actualUser.getName());
        assertEquals(1, actualUser.getFriends().size());
        //проверка создания с непустым id
        assertThrows(IllegalArgumentException.class, () -> storage.create(actualUser));
    }

    @Test
    void updateOk() {
        user = storage.create(user);

        user.setName("New Name");
        Optional<User> optUser = storage.update(user);
        assertEquals(user, optUser.orElse(null));
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

        Optional<User> optUser = storage.update(unknownUser);
        assertTrue(optUser.isEmpty());
    }

    @Test
    void updateEmptyId() {
        storage.create(user);

        User unknownUser = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

        assertThrows(NullPointerException.class, () -> storage.update(unknownUser));
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
        Optional<User> optUser = storage.getById(user.getId());

        assertEquals(user, optUser.orElse(null));
    }

    @Test
    void testSaveFriendship() {
        User user2 = storage.create(
                User.builder()
                        .login("user2_login")
                        .name("User2 Name")
                        .email("user2@mail.ru")
                        .birthday(LocalDate.of(1980, 12, 1))
                        .build());
        user = storage.create(user);
        storage.saveFriendship(user.getId(), user2.getId());
        user = storage.getById(user.getId()).orElse(null);
        assertNotNull(user);
        assertEquals(1, user.getFriends().size());

        assertThrows(NullPointerException.class, () -> storage.saveFriendship(null, null));
    }
}