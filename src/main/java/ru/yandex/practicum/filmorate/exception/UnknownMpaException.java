package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UnknownMpaException extends RuntimeException {
    public UnknownMpaException(String m) {
        super(m);
    }
}
