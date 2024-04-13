package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DictionaryServiceTest {
    private final DictionaryService service;

    @Autowired
    public DictionaryServiceTest(DictionaryService service) {
        this.service = service;
    }

    @Test
    void getAllGenres() {
        assertFalse(service.getAllGenres().isEmpty());
    }

    @Test
    void getGenreById() {
        assertNotNull(service.getGenreById(1));
    }

    @Test
    void getMpaById() {
        assertNotNull(service.getMpaById(1));
    }

    @Test
    void getAllMpa() {
        assertFalse(service.getAllMpa().isEmpty());
    }
}