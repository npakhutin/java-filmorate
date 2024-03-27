package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User create(User user) {
        return storage.create(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public List<User> getAll() {
        return storage.getAll();
    }

    public User getById(Integer id) {
        return storage.getById(id);
    }

    public User addFriend(Integer id, Integer friendId) {
        User user = getById(id);
        User friend = getById(friendId);

        user.addFriend(friend.getId());
        friend.addFriend(user.getId());
        return user;
    }

    public User deleteFriend(Integer id, Integer friendId) {
        User user = getById(id);
        User friend = getById(friendId);
        user.deleteFriend(friend.getId());
        friend.deleteFriend(user.getId());
        return user;
    }

    public List<User> getFriends(Integer id) {
        User user = getById(id);
        List<Integer> friendIds = user.getFriendIds();

        return friendIds.stream()
                .map(storage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer otherId) {
        List<User> friends = getFriends(id);
        List<User> otherFriends = getFriends(otherId);

        friends.retainAll(otherFriends);
        return friends;
    }
}
