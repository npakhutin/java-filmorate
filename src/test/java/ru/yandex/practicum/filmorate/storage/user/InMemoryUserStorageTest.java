package ru.yandex.practicum.filmorate.storage.user;

import org.junit.jupiter.api.BeforeEach;

class InMemoryUserStorageTest extends UserStorageTest<UserStorage> {

    @BeforeEach
    void setUp() {
        super.setUp();
        storage = new InMemoryUserStorage();
    }
}