package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
@JsonIdentityInfo(property = "id", generator = ObjectIdGenerators.PropertyGenerator.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User extends IdentifiedModelObject {
    private final Set<User> friends = new HashSet<>();
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

    public String getName() {
        return name == null || name.isEmpty() ? login : name;
    }

    public List<User> getFriends() {
        return List.copyOf(friends);
    }

    public void addFriend(@NotNull User user) {
        if (Objects.equals(user, this)) {
            throw new IllegalArgumentException("Невозможно добавить самого себя в список друзей, id = " + user.getId());
        }
        friends.add(user);
    }

    public void deleteFriend(User user) {
        friends.remove(user);
    }
}
