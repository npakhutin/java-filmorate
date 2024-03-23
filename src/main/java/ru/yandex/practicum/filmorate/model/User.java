package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Null(groups = {Transfer.New.class})
    @NotNull(groups = {Transfer.Existing.class})
    @Setter(AccessLevel.NONE)
    private Integer id;

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

    public void setId(Integer id) {
        if (this.id != null) {
            throw new IllegalArgumentException("Id has already been set");
        }
        this.id = id;
    }

    public String getName() {
        return name == null || name.isEmpty() ? login : name;
    }

    public List<Integer> getFriendIds() {
        return List.copyOf(friendIds);
    }

    public void addFriend(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User.id should be not null");
        }
        if (Objects.equals(user.getId(), this.getId())) {
            throw new IllegalArgumentException("Can't add self as a friend");
        }
        friendIds.add(user.getId());
    }

    public void deleteFriend(User user) {
        friendIds.remove(user.getId());
    }
}
