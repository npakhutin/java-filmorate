package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Null(groups = {Transfer.New.class})
    @NotNull(groups = {Transfer.Existing.class})
    private Integer id;

    @NotBlank
    @Pattern(regexp="^[\\w.-]{0,19}[0-9a-zA-Z]$")
    private String login;

    @Getter(AccessLevel.NONE)
    private String name;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;

    @PastOrPresent
    private LocalDate birthday;

    public String getName() {
        return name == null || name.isEmpty() ? login : name;
    }
}
