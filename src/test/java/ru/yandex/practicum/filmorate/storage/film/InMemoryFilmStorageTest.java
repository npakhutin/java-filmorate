package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.BeforeEach;

class InMemoryFilmStorageTest extends FilmStorageTest<InMemoryFilmStorage> {

    @BeforeEach
    void setUp() {
        super.setUp();
        storage = new InMemoryFilmStorage();
    }
}