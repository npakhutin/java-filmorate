package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class IdentifiedModelObjectTest<T extends IdentifiedModelObject> {
    protected T entity;

    @Test
    void setId() {
        entity.setId(1);
        assertEquals(1, entity.getId());
        assertThrows(IllegalArgumentException.class, () -> entity.setId(2));
    }
}