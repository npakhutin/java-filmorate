package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UnknownUserException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistsException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();
    private Integer userIdCounter = 0;

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        log.info("POST /users");
        if (user.getId() != null) {
            log.warn("Error: new user id should be null, actual id = " + user.getId());
            throw new UserAlreadyExistsException();
        }
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("User added with id {}", user.getId());
        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user) {
        log.info("PUT /users");
        if (user.getId() == null || !users.containsKey(user.getId())) {
            log.warn("Unknown user with id = " + user.getId());
            throw new UnknownUserException();
        }
        users.put(user.getId(), user);
        log.info("User with id {} updated", user.getId());
        return user;
    }

    @GetMapping("/users")
    public Collection<User> getAll() {
        log.info("GET /users");
        return users.values();
    }

    private int getNextUserId() {
        return ++userIdCounter;
    }

}
