package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Component
public class UserMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friendList(getFriends(rs.getLong("user_id")))
                .build();
    }

    private List<Long> getFriends(long id) {
        String sql = "SELECT * FROM friendships WHERE user_id = ?";
        List<Long> friendIds = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> rs.getLong("friend_id"));
        if (friendIds.isEmpty()) {
            return Collections.emptyList();
        }
        return friendIds;
    }
}

