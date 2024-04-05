package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Director> directorRowMapper = ((rs, rowNum) -> Director.builder()
            .id(rs.getInt("director_id"))
            .name(rs.getString("name"))
            .build());
    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT * FROM directors";
        return jdbcTemplate.query(sql, directorRowMapper);
    }
    @Override
    public Optional<Director> getById(long id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
        return Optional.of(jdbcTemplate.queryForObject(sql, directorRowMapper, id));
    }
    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        Map<String, Object> values = new HashMap<>();
        values.put("name", director.getName());
        int id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        director.setId(id);
        return director;
    }
    @Override
    public Director update(Director director) {
        String sql = "UPDATE directors SET name = ?";
        jdbcTemplate.update(sql, director.getName());
        return director;
    }
    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
