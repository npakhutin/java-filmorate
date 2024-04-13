package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class DictionaryDbStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DictionaryDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Genre> getAllGenres() {
        String sql = "select ID, NAME from GENRE";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT ID, NAME FROM GENRE WHERE ID = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Genre> getGenresByFilmId(Integer filmId) {
        String sql = "SELECT G.ID, G.NAME " +
                "FROM GENRE G JOIN GENRE_FILM GF ON GF.GENRE_ID = G.ID " +
                "WHERE GF.FILM_ID = ?" +
                "ORDER BY G.ID";

        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    public Optional<MpaRating> getMpaById(Integer id) {
        String sql = "SELECT ID, NAME FROM MPA WHERE ID = ?";

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<MpaRating> getAllMpa() {
        String sql = "select ID, NAME from MPA";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .build();
    }

    private MpaRating mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder()
                .id(rs.getInt("ID"))
                .name(rs.getString("NAME"))
                .build();
    }
}
