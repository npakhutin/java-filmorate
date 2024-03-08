package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.constraints.MinimalDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @MinimalDate(minDate = "28.12.1895", dateFormat = "dd.MM.yyyy")
    private LocalDate releaseDate;
    @Positive
    private Integer duration;
}
