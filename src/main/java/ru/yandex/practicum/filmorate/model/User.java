package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class User extends IdentifiedModelObject {
    @NotBlank
    @Pattern(regexp = "^[\\w.-]{0,19}[0-9a-zA-Z]$")
    private String login;

    @Getter(AccessLevel.NONE)
    private String name;

    @Email(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    private String email;

    @PastOrPresent
    private LocalDate birthday;

    private final Set<Integer> friendIds = new HashSet<>();

    public String getName() {
        return name == null || name.isEmpty() ? login : name;
    }

    public List<Integer> getFriendIds() {
        return List.copyOf(friendIds);
    }

    public void addFriend(@NotNull Integer userId) {
        if (Objects.equals(userId, this.getId())) {
            throw new IllegalArgumentException("Невозможно добавить самого себя в список друзей, id = " + userId);
        }
        friendIds.add(userId);
    }

    public void deleteFriend(Integer userId) {
        friendIds.remove(userId);
    }
}
