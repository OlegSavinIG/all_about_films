package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.storage.GenreStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Genre> genreRowMapper = (rs, rowNum) ->
            new Genre(rs.getInt("genre_id"), rs.getString("name"));
    private final String sqlGetAll = "SELECT * FROM genres";
    private final String sqlFindById = "SELECT * FROM genres WHERE genre_id = ?";


    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        log.info("Выполнение запроса на получение всех жанров.");
        return jdbcTemplate.query(sqlGetAll, genreRowMapper);
    }

    @Override
    public Optional<Genre> findById(int id) {
//        String sqlChecker = "Select genre_id from genres where genre_id = ?";
//        List<Integer> genreId = jdbcTemplate.query(sqlChecker, new Object[]{id}, (rs, rowNum) -> rs.getInt("genre_id"));
//        if (genreId.isEmpty()) {
//            throw new NotExistException("Не существует жанра с таким id " + id);
//        }
        log.info("Выполнение запроса на получение жанра с ID: {}", id);

        return jdbcTemplate.query(sqlFindById, genreRowMapper, id).stream().findAny();
    }
}
