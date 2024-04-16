package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
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
        String sql = "select ID, NAME from GENRES";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public Optional<Genre> getGenreById(Integer id) {
        String sql = "SELECT ID, NAME FROM GENRES WHERE ID = ?";

        List<Genre> genres = jdbcTemplate.query(sql, this::mapRowToGenre, id);
        if (genres.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(genres.get(0));
        }
    }

    public List<Genre> getGenresByFilmId(Integer filmId) {
        String sql = "SELECT G.ID, G.NAME " +
                "FROM GENRES G JOIN GENRES_FILMS GF ON GF.GENRE_ID = G.ID " +
                "WHERE GF.FILM_ID = ?" +
                "ORDER BY G.ID";

        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    public Optional<MpaRating> getMpaById(Integer id) {
        String sql = "SELECT ID, NAME FROM MPA WHERE ID = ?";

        List<MpaRating> mpaRatings = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        if (mpaRatings.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(mpaRatings.get(0));
        }
    }

    public List<MpaRating> getAllMpa() {
        String sql = "select ID, NAME from MPA";
        return jdbcTemplate.query(sql, this::mapRowToMpa);
    }

    private Genre mapRowToGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder().id(rs.getInt("ID")).name(rs.getString("NAME")).build();
    }

    private MpaRating mapRowToMpa(ResultSet rs, int rowNum) throws SQLException {
        return MpaRating.builder().id(rs.getInt("ID")).name(rs.getString("NAME")).build();
    }
}
