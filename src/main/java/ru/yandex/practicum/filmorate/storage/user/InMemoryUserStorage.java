package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
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
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(users.values());
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    private int getNextUserId() {
        return ++userIdCounter;
    }
}
