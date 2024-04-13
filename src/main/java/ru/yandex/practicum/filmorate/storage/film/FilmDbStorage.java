package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DictionaryDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final DictionaryDbStorage dictionaryStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, DictionaryDbStorage dictionaryStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.dictionaryStorage = dictionaryStorage;
    }

    @Override
    public Film create(Film film) {
        if (film.getId() != null) {
            throw new NullPointerException("Поле id создаваемого фильма должно быть пустым");
        }
        String sql = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " + "VALUES(?, ?, ?, ?, ?);";

        Integer mpaId = Optional.ofNullable(film.getMpa()).map(MpaRating::getId).orElse(null);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, mpaId, Types.INTEGER);
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKeyAs(Integer.class));
        saveRelatedObjects(film);

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (film.getId() == null) {
            throw new NullPointerException("Поле id обновляемого фильма не должно быть пустым");
        }
        String sql = "UPDATE FILM SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, MPA_ID=? WHERE ID=?;";

        int rowCount = jdbcTemplate.update(sql,
                                           film.getName(),
                                           film.getDescription(),
                                           film.getReleaseDate(),
                                           film.getDuration(),
                                           Optional.ofNullable(film.getMpa()).map(MpaRating::getId).orElse(null),
                                           film.getId());
        if (rowCount == 0) {
            return Optional.empty();
        } else {
            saveRelatedObjects(film);
            return Optional.of(film);
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID FROM FILM";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        for (Film film : films) {
            loadRelatedObjects(film);
        }
        return films;
    }

    @Override
    public Optional<Film> getById(Integer id) {
        String sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID FROM FILM WHERE ID = ?";

        try {
            Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, id);
            assert film != null;
            loadRelatedObjects(film);
            return Optional.of(film);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            throw new NullPointerException("Идентификаторы фильма и пользователя не должны быть пустыми");
        }
        String sql = "MERGE INTO LIKES(USER_ID, FILM_ID) KEY(USER_ID, FILM_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            throw new NullPointerException("Идентификаторы фильма и пользователя не должны быть пустыми");
        }
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .description(rs.getString("DESCRIPTION"))
                .releaseDate(rs.getDate("RELEASE_DATE").toLocalDate())
                .duration(rs.getInt("DURATION"))
                .mpa(dictionaryStorage.getMpaById(rs.getInt("MPA_ID")).orElse(null))
                .build();
    }

    private void loadRelatedObjects(Film film) {
        loadFilmLikes(film);
        loadFilmGenres(film);
    }

    private void loadFilmLikes(Film film) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";

        List<Integer> userIds =
                jdbcTemplate.query(sql, (resultSet, rowNum) -> resultSet.getInt("USER_ID"), film.getId());
        for (Integer id : userIds) {
            film.addLike(id);
        }
    }

    private void saveRelatedObjects(Film film) {
        saveLikes(film);
        saveGenres(film);
    }

    private void saveLikes(Film film) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film.getId());

        for (Integer userId : film.getUsersLiked()) {
            saveLike(film.getId(), userId);
        }
    }

    private void saveGenres(Film film) {
        String sql = "DELETE FROM GENRE_FILM WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film.getId());

        for (Genre genre : film.getGenres()) {
            saveFilmGenre(genre.getId(), film.getId());
        }
    }

    private void saveFilmGenre(Integer genreId, Integer filmId) {
        if (filmId == null || genreId == null) {
            throw new NullPointerException("Идентификаторы фильма и жанра не должны быть пустыми");
        }
        String sql = "MERGE INTO GENRE_FILM(GENRE_ID, FILM_ID) KEY(GENRE_ID, FILM_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, genreId, filmId);

    }

    private void loadFilmGenres(Film film) {
        List<Genre> filmGenres = dictionaryStorage.getGenresByFilmId(film.getId());
        for (Genre genre : filmGenres) {
            film.addGenre(genre);
        }
    }
}
