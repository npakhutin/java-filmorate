package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.UnknownModelObjectException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(scripts = {"classpath:del_tables.sql", "classpath:schema.sql", "classpath:data.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private final UserService service;
    private User user;

    @Autowired
    public UserServiceTest(UserService service) {
        this.service = service;
    }

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
        assertThrows(UnknownModelObjectException.class, () -> service.getById(user.getId() + 1));
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

        service.addFriend(user.getId(), friend.getId());
        List<User> actualFriends = service.getUserFriends(user.getId());
        assertEquals(List.of(friend), actualFriends);

        service.deleteFriend(user.getId(), friend.getId());
        actualFriends = service.getUserFriends(user.getId());
        assertEquals(0, actualFriends.size());
    }
}