package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Integer userIdCounter = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UnknownUserException("В хранилище не найден пользователь с id = " + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User getById(Integer id) {
        if (!users.containsKey(id)) {
            throw new UnknownUserException("В хранилище не найден пользователь с id = " + id);
        }
        return users.get(id);
    }

    private int getNextUserId() {
        return ++userIdCounter;
    }
}
