package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User create(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("Поле id создаваемого пользователя должно быть пустым");
        }
        String sql = "INSERT INTO USERS (LOGIN, NAME, EMAIL, BIRTHDAY) VALUES(?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKeyAs(Integer.class));
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        if (user.getId() == null) {
            throw new NullPointerException("Поле id обновляемого пользователя не должно быть пустым");
        }
        String sql = "UPDATE USERS SET LOGIN=?, NAME=?, EMAIL=?, BIRTHDAY=? WHERE ID=?;";

        int rowCount = jdbcTemplate.update(sql,
                                           user.getLogin(),
                                           user.getName(),
                                           user.getEmail(),
                                           user.getBirthday(),
                                           user.getId());
        if (rowCount == 0) {
            return Optional.empty();
        } else {
            return Optional.of(user);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM USERS;";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public Optional<User> getById(int id) {
        String sql = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM USERS WHERE ID = ?";

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(users.get(0));
    }

    @Override
    public void saveFriendship(int requesterId, int responderId) {
        String sql = "MERGE INTO FRIENDS(REQUESTER_ID, RESPONDER_ID) KEY(REQUESTER_ID, RESPONDER_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, requesterId, responderId);
    }

    @Override
    public void deleteFriendship(int requesterId, int responderId) {
        String sql = "DELETE FROM FRIENDS WHERE REQUESTER_ID = ? AND RESPONDER_ID = ?;";
        jdbcTemplate.update(sql, requesterId, responderId);
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        String sql =
                "SELECT u.* FROM USERS u\n" +
                "JOIN (\n" +
                "    SELECT RESPONDER_ID FROM FRIENDS f WHERE REQUESTER_ID = :first_user\n" +
                "        AND RESPONDER_ID <> :second_user\n" +
                "    UNION \n" +
                "    SELECT RESPONDER_ID FROM FRIENDS f WHERE REQUESTER_ID = :second_user\n" +
                "        AND RESPONDER_ID <> :first_user\n" +
                ") ff ON u.id = ff.RESPONDER_ID;\n";

        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(jdbcTemplate.getDataSource());
        MapSqlParameterSource paramSource = new MapSqlParameterSource();
        paramSource.addValue("first_user", id);
        paramSource.addValue("second_user", otherId);
        return namedParameterJdbcTemplate.query(sql, paramSource, this::mapRowToUser);
    }

    @Override
    public List<User> getUserFriends(int userId) {
        String sql =
                "SELECT u.* FROM USERS u\n" +
                "JOIN FRIENDS f ON u.ID = f.RESPONDER_ID \n" +
                "WHERE f.REQUESTER_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToUser, userId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("ID"))
                .login(rs.getString("LOGIN"))
                .name(rs.getString("NAME"))
                .email(rs.getString("EMAIL"))
                .birthday(rs.getDate("BIRTHDAY").toLocalDate())
                .build();
    }

}
