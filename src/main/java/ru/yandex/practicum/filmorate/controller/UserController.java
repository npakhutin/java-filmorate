package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Integer userIdCounter = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Validated(Transfer.New.class) @RequestBody User user) {
        log.info("POST /users");

        user.setId(getNextUserId());
        users.put(user.getId(), user);

        log.info("User added with id {}", user.getId());
        return user;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Validated(Transfer.Existing.class) @RequestBody User user) {
        log.info("PUT /users");

        if (!users.containsKey(user.getId())) {
            var m = "Unknown user with id = " + user.getId();
            log.warn(m);
            throw new UnknownUserException(m);
        }
        users.put(user.getId(), user);

        log.info("User with id {} updated", user.getId());
        return user;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        log.info("GET /users");
        return List.copyOf(users.values());
    }

    private int getNextUserId() {
        return ++userIdCounter;
    }

}
