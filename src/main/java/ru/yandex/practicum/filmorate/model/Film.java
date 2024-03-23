package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.constraints.MinimalDate;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Null(groups = {Transfer.New.class})
    @NotNull(groups = {Transfer.Existing.class})
    @Setter(AccessLevel.NONE)
    private Integer id;

    @NotBlank
    private String name;

    @Size(max = 200)
    @NotBlank
    private String description;

    @MinimalDate(minDate = "28.12.1895", dateFormat = "dd.MM.yyyy")
    @PastOrPresent
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;

    private final Set<Integer> usersLiked = new HashSet<>();

    public void setId(Integer id) {
        if (this.id != null) {
            throw new IllegalArgumentException("Id has already been set");
        }
        this.id = id;
    }

    public void setLike(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id should be not null");
        }
        usersLiked.add(userId);
    }

    public void deleteLike(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User id should be not null");
        }
        usersLiked.remove(userId);
    }
}
