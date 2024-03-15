package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
    @Null(groups = {Transfer.New.class})
    @NotNull(groups = {Transfer.Existing.class})
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
}
