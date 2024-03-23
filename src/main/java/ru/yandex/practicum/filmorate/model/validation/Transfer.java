package ru.yandex.practicum.filmorate.model.validation;

import javax.validation.groups.Default;

public interface Transfer {
    interface New extends Default { }

    interface Existing extends Default { }
}
