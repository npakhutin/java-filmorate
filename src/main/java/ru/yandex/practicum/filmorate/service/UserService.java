package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownModelObjectException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user)
                .orElseThrow(() -> new UnknownModelObjectException("В хранилище не найден пользователь для обновления с id = " + user.getId()));
    }

    public List<User> getAll() {
        return storage.getAll();
    }

    public User getById(int id) {
        return storage.getById(id)
                .orElseThrow(() -> new UnknownModelObjectException("В хранилище не найден пользователь с id = " + id));
    }

    public void addFriend(int id, int friendId) {
        if (id == friendId) {
            throw new IllegalArgumentException(
                    "Получены одинаковые идентификаторы пользователей для добавления в друзья, id = " + id);
        }
        getById(id);
        getById(friendId);
        storage.saveFriendship(id, friendId);
    }

    public void deleteFriend(int id, int friendId) {
        if (id == friendId) {
            throw new IllegalArgumentException(
                    "Получены одинаковые идентификаторы пользователей для удаления из друзей, id = " + id);
        }
        getById(id);
        getById(friendId);
        storage.deleteFriendship(id, friendId);
    }

    public List<User> getUserFriends(int userId) {
        getById(userId);
        return storage.getUserFriends(userId);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        getById(id);
        getById(otherId);
        return storage.getCommonFriends(id, otherId);
    }
}
