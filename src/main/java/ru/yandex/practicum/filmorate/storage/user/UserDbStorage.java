package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
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
        String sql = "INSERT INTO \"USER\" (LOGIN, NAME, EMAIL, BIRTHDAY) VALUES(?, ?, ?, ?);";

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
        saveFriendship(user);
        return user;
    }

    @Override
    public Optional<User> update(User user) {
        if (user.getId() == null) {
            throw new NullPointerException("Поле id обновляемого пользователя не должно быть пустым");
        }
        String sql = "UPDATE \"USER\" SET LOGIN=?, NAME=?, EMAIL=?, BIRTHDAY=? WHERE ID=?;";

        int rowCount = jdbcTemplate.update(sql,
                                           user.getLogin(),
                                           user.getName(),
                                           user.getEmail(),
                                           user.getBirthday(),
                                           user.getId());
        if (rowCount == 0) {
            return Optional.empty();
        } else {
            saveFriendship(user);
            return Optional.of(user);
        }
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM \"USER\";";

        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        for (User user : users) {
            loadFriendship(user);
        }
        return users;
    }

    @Override
    public Optional<User> getById(Integer id) {
        String sql = "SELECT ID, LOGIN, NAME, EMAIL, BIRTHDAY FROM \"USER\" WHERE ID = ?";

        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
            assert user != null;
            loadFriendship(user);
            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveFriendship(Integer requesterId, Integer responderId) {
        if (requesterId == null || responderId == null) {
            throw new NullPointerException("Идентификаторы пользователей для добавления в друзья не должны быть пустыми");
        }
        String sql = "MERGE INTO FRIENDSHIP(REQUESTER_ID, RESPONDER_ID) KEY(REQUESTER_ID, RESPONDER_ID) VALUES(?, ?);";
        jdbcTemplate.update(sql, requesterId, responderId);
    }

    @Override
    public void deleteFriendship(Integer requesterId, Integer responderId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE REQUESTER_ID = ? AND RESPONDER_ID = ?;";
        jdbcTemplate.update(sql, requesterId, responderId);
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

    private void saveFriendship(User user) {
        String sql = "DELETE FROM FRIENDSHIP WHERE REQUESTER_ID = ?;";
        jdbcTemplate.update(sql, user.getId());

        for (User friend : user.getFriends()) {
            saveFriendship(user.getId(), friend.getId());
        }
    }

    private void loadFriendship(User user) {
        String sql =
                "SELECT U.ID, U.LOGIN, U.NAME, U.EMAIL, U.BIRTHDAY FROM \"USER\" U\n" + "JOIN FRIENDSHIP F ON U.ID = F.RESPONDER_ID \n" + "WHERE F.REQUESTER_ID = ?";
        List<User> friends = jdbcTemplate.query(sql, this::mapRowToUser, user.getId());
        for (User friend : friends) {
            user.addFriend(friend);
        }
    }

}
