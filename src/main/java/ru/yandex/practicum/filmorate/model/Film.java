package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.constraints.MinimalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Film extends IdentifiedModelObject {
    private final Set<Genre> genres = new HashSet<>();
    private final Set<Integer> usersLiked = new HashSet<>();
    @Size(max = 100)
    @NotBlank
    private String name;
    @Size(max = 200)
    @NotBlank
    private String description;
    @MinimalDate(minDate = "28.12.1895", dateFormat = "dd.MM.yyyy")
    @PastOrPresent
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private MpaRating mpa;

    public void addLike(Integer userId) {
        usersLiked.add(userId);
    }

    public void deleteLike(Integer userId) {
        usersLiked.remove(userId);
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }
}
