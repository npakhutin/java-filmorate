package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UnknownUserException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validation.Transfer;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Validated(Transfer.New.class) @RequestBody User user) {
        log.info("POST /users");
        user = service.create(user);
        log.info("User added with id {}", user.getId());
        return user;
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Validated(Transfer.Existing.class) @RequestBody User user) {
        log.info("PUT /users");

        try {
            user = service.update(user);
        } catch (UnknownUserException e) {
            log.warn(e.getMessage());
            throw e;
        }

        log.info("User with id {} updated", user.getId());
        return user;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        log.info("GET /users");
        return service.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getById(@PathVariable Integer id) {
        log.info("GET /users/{}", id);
        return service.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("PUT /users/{}/friends/{}", id, friendId);

        try {
            return service.addFriend(id, friendId);
        } catch (UnknownUserException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("DELETE /users/{}/friends/{}", id, friendId);

        try {
            return service.deleteFriend(id, friendId);
        } catch (UnknownUserException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("GET /users/{}/friends", id);

        try {
            return service.getFriends(id);
        } catch (UnknownUserException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("GET /users/{}/friends/common/{}", id, otherId);

        try {
            return service.getCommonFriends(id, otherId);
        } catch (UnknownUserException e) {
            log.warn(e.getMessage());
            throw e;
        }
    }

}
