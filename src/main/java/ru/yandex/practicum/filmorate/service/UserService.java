package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new UnknownUserException("В хранилище не найден пользователь для обновления с id = " + user.getId()));
    }

    public List<User> getAll() {
        return storage.getAll();
    }

    public User getById(Integer id) {
        return storage.getById(id)
                .orElseThrow(() -> new UnknownUserException("В хранилище не найден пользователь с id = " + id));
    }

    public List<User> addFriend(Integer id, Integer friendId) {
        if (id.equals(friendId)) {
            throw new IllegalArgumentException(
                    "Получены одинаковые идентификаторы пользователей для добавления в друзья, id = " + id);
        }
        getById(id);
        getById(friendId);
        storage.saveFriendship(id, friendId);
        User user = storage.getById(id)
                .orElseThrow(() -> new UnknownUserException("В хранилище не найден пользователь с id = " + id));

        return user.getFriends();
    }

    public List<User> deleteFriend(Integer id, Integer friendId) {
        if (id.equals(friendId)) {
            throw new IllegalArgumentException(
                    "Получены одинаковые идентификаторы пользователей для удаления из друзей, id = " + id);
        }
        getById(id);
        getById(friendId);
        storage.deleteFriendship(id, friendId);
        User user = storage.getById(id)
                .orElseThrow(() -> new UnknownUserException("В хранилище не найден пользователь с id = " + id));
        return user.getFriends();
    }

    public List<User> getFriends(Integer id) {
        User user = storage.getById(id)
                .orElseThrow(() -> new UnknownUserException("В хранилище не найден пользователь с id = " + id));
        return user.getFriends();
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> friends = getFriends(id);
        List<User> otherFriends = getFriends(otherId);

        return friends.stream().filter(otherFriends::contains).collect(Collectors.toList());
    }
}
