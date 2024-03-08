package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    private Integer id;
    @NotBlank
    private String login;
    @Getter(AccessLevel.NONE)
    private String name;
    @Email
    private String email;
    @Past
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isEmpty() ? login : name;
    }
}
