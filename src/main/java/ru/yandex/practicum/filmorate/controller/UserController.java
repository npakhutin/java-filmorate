package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
public class UserController {
    private Integer userIdCounter = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @PostMapping("/users")
    public ResponseEntity<User> create(@Validated(Transfer.New.class) @RequestBody User user) {
        log.info("POST /users");

        user.setId(getNextUserId());
        users.put(user.getId(), user);

        log.info("User added with id {}", user.getId());
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/users")
    public ResponseEntity<User> update(@Validated(Transfer.Existing.class) @RequestBody User user) {
        log.info("PUT /users");

        if (!users.containsKey(user.getId())) {
            var m = "Unknown user with id = " + user.getId();
            log.warn(m);
            throw new UnknownUserException(m);
        }
        users.put(user.getId(), user);

        log.info("User with id {} updated", user.getId());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAll() {
        log.info("GET /users");
        return new ResponseEntity<>(List.copyOf(users.values()), HttpStatus.OK);
    }

    private int getNextUserId() {
        return ++userIdCounter;
    }

}
