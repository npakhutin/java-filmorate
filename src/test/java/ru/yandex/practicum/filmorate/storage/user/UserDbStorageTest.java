package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(scripts = {"classpath:del_tables.sql", "classpath:schema.sql", "classpath:data.sql"}, executionPhase = BEFORE_TEST_METHOD)
public class UserDbStorageTest extends UserStorageTest<UserDbStorage> {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        super.setUp();
        storage = new UserDbStorage(jdbcTemplate);
    }
}
