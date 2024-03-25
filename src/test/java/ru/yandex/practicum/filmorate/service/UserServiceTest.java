package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {
    private final UserService service = new UserService(new InMemoryUserStorage());
    private User user;

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
    void create() {
        service.create(user);
        assertEquals(user, service.getById(user.getId()));
    }

    @Test
    void update() {
        user = service.create(user);

        user = User.builder()
                .id(user.getId())
                .login("user2_login")
                .name("User2 Name")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        User updatedUser = service.update(user);
        assertEquals(user.getName(), updatedUser.getName());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getAll() {
        service.create(user);
        user = User.builder()
                .login("user2_login")
                .name("User2 Name")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
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
        User friend = User.builder()
                .login("friend_login")
                .name("Friend Name")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

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