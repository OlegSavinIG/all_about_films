package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userRowMapper;
    private final String sqlAdd = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
    private final String sqlUpdate = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
    private final String sqlDelete = "DELETE FROM users WHERE user_id = ?";
    private final String sqlGetAll = "SELECT * FROM users";
    private final String sqlGetById = "SELECT * FROM users WHERE user_id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserMapper userRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRowMapper = userRowMapper;
    }

    @Override
    public User add(User user) {
        log.info("Добавление пользователя: {}", user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlAdd, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        log.debug("Пользователь добавлен с ID: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Обновление пользователя с ID: {}", user.getId());
        jdbcTemplate.update(sqlUpdate, user.getEmail(), user.getLogin(), user.getName(), Date.valueOf(user.getBirthday()), user.getId());
        log.debug("Пользователь обновлен: {}", user);
        return user;
    }

    @Override
    public void delete(long id) {
        log.info("Удаление пользователя с ID: {}", id);
        jdbcTemplate.update(sqlDelete, id);
        log.debug("Пользователь удален с ID: {}", id);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return jdbcTemplate.query(sqlGetAll, userRowMapper);
    }

    @Override
    public Optional<User> getById(long id) {
        String sqlChecker = "Select user_id from users where user_id = ?";
        List<Long> userId = jdbcTemplate.query(sqlChecker, new Object[]{id}, (rs, rowNum) -> rs.getLong("user_id"));
        if (userId.isEmpty()) {
            throw new NotExistException("Не существует пользователя с таким id " + id);
        }
        log.info("Получение пользователя с ID: {}", id);
        User user = jdbcTemplate.queryForObject(sqlGetById, userRowMapper, id);
        return Optional.ofNullable(user);
    }

    public Long getCommonUserByLikes(long userId) {
        String sql = "SELECT user_id, COUNT(*) as common_likes " +
                "FROM likes " +
                "WHERE film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "AND user_id <> ? " +
                "GROUP BY user_id " +
                "ORDER BY common_likes DESC " +
                "LIMIT 1";
        List<Long> commonUserId = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), userId, userId);
        if (commonUserId.isEmpty()) {
            return null;
        }
        return commonUserId.stream().findFirst().get();
    }

}
