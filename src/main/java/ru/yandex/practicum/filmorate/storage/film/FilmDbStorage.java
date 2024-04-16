package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.IdentifiedModelObject;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.DictionaryDbStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        String sql = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " + "VALUES(?, ?, ?, ?, ?);";

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
        saveFilmGenres(film);

        return film;
    }

    @Override
    public Optional<Film> update(Film film) {
        if (film.getId() == null) {
            throw new NullPointerException("Поле id обновляемого фильма не должно быть пустым");
        }
        String sql = "UPDATE FILMS SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, MPA_ID=? WHERE ID=?;";

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
            saveFilmGenres(film);
            return Optional.of(film);
        }
    }

    @Override
    public List<Film> getAll() {
        String sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID FROM FILMS";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);

        sql = "SELECT gf.FILM_ID, g.* FROM GENRES g \n" +
                "JOIN GENRES_FILMS gf ON g.ID = gf.GENRE_ID \n" +
                "WHERE gf.FILM_ID IN (:ids);";
        Set<Integer> ids = films.stream().map(IdentifiedModelObject::getId).collect(Collectors.toSet());

        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("ids", ids);

        Map<Integer, List<Genre>> filmsGenres = namedParameterJdbcTemplate.query(
                sql, parameters, rs -> {
                    Map<Integer, List<Genre>> map = new HashMap<>();
                    while (rs.next()) {
                        Integer filmId = rs.getInt("FILM_ID");
                        map.put(filmId, new ArrayList<>());
                        Genre genre = Genre.builder().id(rs.getInt("ID")).name(rs.getString("NAME")).build();
                        map.get(filmId).add(genre);
                    }
                    return map;
                });

        if (filmsGenres != null && !filmsGenres.isEmpty()) {
            for (Film film : films) {
                if (filmsGenres.containsKey(film.getId())) {
                    for (Genre genre : filmsGenres.get(film.getId())) {
                        film.addGenre(genre);
                    }
                }
            }
        }
        return films;
    }

    @Override
    public Optional<Film> getById(int id) {
        String sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID FROM FILMS WHERE ID = ?";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        Film film = films.get(0);
        List<Genre> filmGenres = dictionaryStorage.getGenresByFilmId(film.getId());
        for (Genre genre : filmGenres) {
            film.addGenre(genre);
        }
        return Optional.of(film);
    }

    @Override
    public void saveLike(int filmId, int userId) {
        String sql = "MERGE INTO LIKES(USER_ID, FILM_ID) KEY(USER_ID, FILM_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?;";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getTopPopular(Integer count) {
        String sql = "SELECT ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID, L.LIKES_COUNT \n" +
                "FROM FILMS F\n" +
                "JOIN (\n" +
                "    SELECT FILM_ID, COUNT(USER_ID) LIKES_COUNT FROM LIKES L\n" +
                "    GROUP BY FILM_ID\n" +
                ") L ON L.FILM_ID = F.ID\n" +
                "ORDER BY L.LIKES_COUNT DESC\n" +
                "LIMIT ?\n";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
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

    private void saveFilmGenres(Film film) {
        String sql = "DELETE FROM GENRES_FILMS WHERE FILM_ID = ?;";
        jdbcTemplate.update(sql, film.getId());

        sql = "MERGE INTO GENRES_FILMS(GENRE_ID, FILM_ID) KEY(GENRE_ID, FILM_ID) VALUES(?, ?);";
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                preparedStatement.setInt(1, genres.get(i).getId());
                preparedStatement.setInt(2, film.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }
}
