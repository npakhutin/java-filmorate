package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    Optional<User> update(User user);

    List<User> getAll();

    Optional<User> getById(Integer id);

    void saveFriendship(Integer requesterId, Integer responderId);

    void deleteFriendship(Integer requesterId, Integer responderId);
}
