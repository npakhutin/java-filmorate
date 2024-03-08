package ru.yandex.practicum.filmorate.model.constraints;

import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

class MinimalDateValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void init() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void checkCorrectDate() {
        MinimalDateChecker checker = new MinimalDateChecker(LocalDate.now());

        Set<ConstraintViolation<MinimalDateChecker>> violations = validator.validate(checker);
        assertEquals(0, violations.size());
    }

    @Test
    void checkTooOldDate() {
        MinimalDateChecker checker = new MinimalDateChecker(LocalDate.of(1800, 1, 1));

        Set<ConstraintViolation<MinimalDateChecker>> violations = validator.validate(checker);
        assertEquals(1, violations.size());
    }

    @Test
    void checkNullDate() {
        MinimalDateChecker checker = new MinimalDateChecker(null);

        Set<ConstraintViolation<MinimalDateChecker>> violations = validator.validate(checker);
        assertEquals(1, violations.size());
    }

    @Data
    static class MinimalDateChecker {
        @MinimalDate(minDate = "28.12.1895", dateFormat = "dd.MM.yyyy")
        private LocalDate date;

        public MinimalDateChecker(LocalDate date) {
            this.date = date;
        }
    }
}