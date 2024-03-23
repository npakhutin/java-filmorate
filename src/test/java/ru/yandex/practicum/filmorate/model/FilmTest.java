package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmTest {
    private static Validator validator;
    private Set<ConstraintViolation<Film>> violations;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @BeforeEach
    void setUp() {

    }

    @Test
    void createIdNotNull() {
        Film film = new Film(1, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        violations = validator.validate(film, Transfer.New.class);
        assertEquals(1, violations.size());
    }

    @Test
    void createIdNull() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        violations = validator.validate(film, Transfer.New.class);
        assertEquals(0, violations.size());
    }

    @Test
    void updateIdNotNull() {
        Film film = new Film(1, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        violations = validator.validate(film, Transfer.Existing.class);
        assertEquals(0, violations.size());
        assertThrows(IllegalArgumentException.class, () -> film.setId(2));
    }

    @Test
    void updateIdNull() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        violations = validator.validate(film, Transfer.Existing.class);
        assertEquals(1, violations.size());
    }

    @Test
    void testValidName() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film.setName("");
        violations = validator.validateProperty(film, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setName(null);
        violations = validator.validateProperty(film, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setName("       ");
        violations = validator.validateProperty(film, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setName("Film Name");
        violations = validator.validateProperty(film, "name", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testValidDescription() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film.setDescription("");
        violations = validator.validateProperty(film, "description", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDescription(null);
        violations = validator.validateProperty(film, "description", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDescription("       ");
        violations = validator.validateProperty(film, "description", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
        violations = validator.validateProperty(film, "description", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDescription("Film Description");
        violations = validator.validateProperty(film, "description", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testReleaseDate() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film.setReleaseDate(null);
        violations = validator.validateProperty(film, "releaseDate", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setReleaseDate(LocalDate.now().plusDays(1));
        violations = validator.validateProperty(film, "releaseDate", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        violations = validator.validateProperty(film, "releaseDate", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        violations = validator.validateProperty(film, "releaseDate", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());

        film.setReleaseDate(LocalDate.of(1895, 12, 29));
        violations = validator.validateProperty(film, "releaseDate", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());

        film.setReleaseDate(LocalDate.now());
        violations = validator.validateProperty(film, "releaseDate", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testDuration() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film.setDuration(null);
        violations = validator.validateProperty(film, "duration", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDuration(-1);
        violations = validator.validateProperty(film, "duration", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDuration(0);
        violations = validator.validateProperty(film, "duration", Transfer.New.class, Transfer.Existing.class);
        assertEquals(1, violations.size());

        film.setDuration(1);
        violations = validator.validateProperty(film, "duration", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());

        film.setDuration(180);
        violations = validator.validateProperty(film, "duration", Transfer.New.class, Transfer.Existing.class);
        assertEquals(0, violations.size());
    }

    @Test
    void testSetLike() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        assertThrows(IllegalArgumentException.class, () -> film.setLike(null));

        film.setLike(1);
        assertEquals(1, film.getUsersLiked().size());

    }

    @Test
    void testDeleteLike() {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);

        assertThrows(IllegalArgumentException.class, () -> film.deleteLike(null));

        film.setLike(1);
        film.deleteLike(1);
        assertEquals(0, film.getUsersLiked().size());

    }
}