package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    private static Validator validator;
    Set<ConstraintViolation<User>> violations;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void createIdNotNull() {
        User user = new User(1, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        violations = validator.validate(user, Transfer.New.class);
        assertEquals(1, violations.size());
    }

    @Test
    void createIdNull() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        violations = validator.validate(user, Transfer.New.class);
        assertEquals(0, violations.size());
    }

    @Test
    void updateIdNotNull() {
        User user = new User(1, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        violations = validator.validate(user, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testUpdateNotNullId() {
        User user = new User(1, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        assertThrows(IllegalArgumentException.class, () -> user.setId(2));
    }

    @Test
    void updateIdNull() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user, Transfer.Existing.class);
        assertEquals(1, violations.size());
    }

    @Test
    void testValidLogin() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        user.setLogin("");
        violations = validator.validateProperty(user, "login", Transfer.New.class, Transfer.Existing.class);
        assertEquals(2, violations.size());

        user.setLogin(null);
        violations = validator.validateProperty(user, "login", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setLogin("       ");
        violations = validator.validateProperty(user, "login", Transfer.New.class, Transfer.Existing.class);
        assertEquals(2, violations.size());

        user.setLogin("user login");
        violations = validator.validateProperty(user, "login", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setLogin("юзер_логин");
        violations = validator.validateProperty(user, "login", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setLogin("user_login");
        violations = validator.validateProperty(user, "login", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testValidName() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        user.setName("");
        violations = validator.validateProperty(user, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
        assertEquals(user.getLogin(), user.getName());

        user.setName(null);
        violations = validator.validateProperty(user, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
        assertEquals(user.getLogin(), user.getName());

        user.setName("User Updated Name");
        violations = validator.validateProperty(user, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
        assertEquals("User Updated Name", user.getName());
    }

    @Test
    void testValidEmail() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        user.setEmail("");
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(2, violations.size());

        user.setEmail(null);
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setEmail("       ");
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(2, violations.size());

        user.setEmail("user@email");
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());

        user.setEmail("user.email.ru@");
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setEmail("юзер@mail.ru");
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setEmail("user@email.ru");
        violations = validator.validateProperty(user, "email", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testBirthday() {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        user.setBirthday(null);
        violations = validator.validateProperty(user, "birthday", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());

        user.setBirthday(LocalDate.now());
        violations = validator.validateProperty(user, "birthday", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());

        user.setBirthday(LocalDate.now().plusDays(1));
        violations = validator.validateProperty(user, "birthday", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        user.setBirthday(LocalDate.of(1995, 12, 27));
        violations = validator.validateProperty(user, "birthday", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testAddFriend() {
        User user = new User(1, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        User friend = new User(2, "friend_login", "Friend Name", "friend@mail.ru", LocalDate.of(1980, 12, 1));
        user.addFriend(friend);

        assertEquals(List.of(friend.getId()), user.getFriendIds());

        assertThrows(IllegalArgumentException.class, () -> user.addFriend(null));
        assertThrows(IllegalArgumentException.class, () -> user.addFriend(user));
    }

    @Test
    void testDeleteFriend() {
        User user = new User(1, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        User friend = new User(2, "friend_login", "Friend Name", "friend@mail.ru", LocalDate.of(1980, 12, 1));
        user.addFriend(friend);

        assertEquals(List.of(friend.getId()), user.getFriendIds());

        user.deleteFriend(friend);
        assertEquals(0, user.getFriendIds().size());
    }
}
