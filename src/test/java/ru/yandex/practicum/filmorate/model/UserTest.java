package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest extends IdentifiedModelObjectTest<User> {
    private static Validator validator;
    private Set<ConstraintViolation<User>> violations;
    private User user;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        entity = user;
    }

    @Test
    void createIdNotNull() {
        user.setId(1);
        violations = validator.validate(user, Transfer.New.class);
        assertEquals(1, violations.size());
    }

    @Test
    void createIdNull() {
        violations = validator.validate(user, Transfer.New.class);
        assertEquals(0, violations.size());
    }

    @Test
    void updateIdNotNull() {
        user.setId(1);
        violations = validator.validate(user, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testUpdateNotNullId() {
        user.setId(1);
        assertThrows(IllegalArgumentException.class, () -> user.setId(2));
    }

    @Test
    void updateIdNull() {
        Set<ConstraintViolation<User>> violations = validator.validate(user, Transfer.Existing.class);
        assertEquals(1, violations.size());
    }

    @Test
    void testValidLogin() {
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
    void testAddFriend() throws Exception {
        user.setId(1);
        User friend = User.builder()
                .id(2)
                .login("friend_login")
                .name("Friend Name")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        user.addFriend(friend);
        assertEquals(List.of(friend), user.getFriends());

        ExecutableValidator executableValidator = Validation
                .buildDefaultValidatorFactory()
                .getValidator()
                .forExecutables();
        Method addFriend = user.getClass().getMethod("addFriend", User.class);
        violations = executableValidator.validateParameters(user, addFriend, new Object[]{null});
        assertEquals(1, violations.size());

        assertThrows(IllegalArgumentException.class, () -> user.addFriend(user));
    }

    @Test
    void testDeleteFriend() {
        user.setId(1);
        User friend = User.builder()
                .id(2)
                .login("friend_login")
                .name("Friend Name")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        user.addFriend(friend);

        assertEquals(List.of(friend), user.getFriends());

        user.deleteFriend(friend);
        assertEquals(0, user.getFriends().size());
    }
}
