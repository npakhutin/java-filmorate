package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.validation.Transfer;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Validated(Transfer.New.class) @RequestBody User user) {
        log.info("POST /users");
        user = service.create(user);
        log.info("Добавлен пользователь с id {}", user.getId());

        return user;
    }

    @PutMapping
    public User update(@Validated(Transfer.Existing.class) @RequestBody User user) {
        log.info("PUT /users");
        user = service.update(user);
        log.info("Обновлен пользователь с id {}", user.getId());

        return user;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("GET /users");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        log.info("GET /users/{}", id);
        return service.getById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("PUT /users/{}/friends/{}", id, friendId);
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("DELETE /users/{}/friends/{}", id, friendId);
        return service.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        log.info("GET /users/{}/friends", id);
        return service.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        log.info("GET /users/{}/friends/common/{}", id, otherId);
        return service.getCommonFriends(id, otherId);
    }

}
