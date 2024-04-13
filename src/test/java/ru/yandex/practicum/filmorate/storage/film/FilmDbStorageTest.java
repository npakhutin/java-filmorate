package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.storage.DictionaryDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

@SpringBootTest
@Sql({"classpath:/schema.sql", "classpath:/data.sql"})
class FilmDbStorageTest extends FilmStorageTest<FilmDbStorage> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        dictionaryStorage = new DictionaryDbStorage(jdbcTemplate);
        storage = new FilmDbStorage(jdbcTemplate, dictionaryStorage);
        userStorage = new UserDbStorage(jdbcTemplate);
        super.setUp();
    }
}