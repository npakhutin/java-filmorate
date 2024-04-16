package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    Optional<User> update(User user);

    List<User> getAll();

    Optional<User> getById(int id);

    void saveFriendship(int requesterId, int responderId);

    void deleteFriendship(int requesterId, int responderId);

    List<User> getCommonFriends(int id, int otherId);

    List<User> getUserFriends(int userId);
}
