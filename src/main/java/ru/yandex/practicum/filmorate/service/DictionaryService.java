package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownGenreException;
import ru.yandex.practicum.filmorate.exception.UnknownMpaException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DictionaryDbStorage;

import java.util.List;

@Service
public class DictionaryService {
    private final DictionaryDbStorage dictionaryStorage;

    @Autowired
    public DictionaryService(DictionaryDbStorage dictionaryStorage) {
        this.dictionaryStorage = dictionaryStorage;
    }

    public List<Genre> getAllGenres() {
        return dictionaryStorage.getAllGenres();
    }

    public Genre getGenreById(Integer id) {
        return dictionaryStorage.getGenreById(id)
                .orElseThrow(() -> new UnknownGenreException("В хранилище не найден жанр с id = " + id));
    }

    public MpaRating getMpaById(Integer id) {
        return dictionaryStorage.getMpaById(id)
                .orElseThrow(() -> new UnknownMpaException("В хранилище не найден рейтинг МРА с id = " + id));
    }

    public List<MpaRating> getAllMpa() {
        return dictionaryStorage.getAllMpa();
    }
}
