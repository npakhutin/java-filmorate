package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.model.validation.Transfer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class IdentifiedModelObject {
    @Null(groups = {Transfer.New.class})
    @NotNull(groups = {Transfer.Existing.class})
    @Setter(AccessLevel.NONE)
    private Integer id;

    public void setId(Integer id) {
        if (this.id != null) {
            throw new IllegalArgumentException("Идентификатор объекта уже задан " + this.id);
        }
        this.id = id;
    }
}
