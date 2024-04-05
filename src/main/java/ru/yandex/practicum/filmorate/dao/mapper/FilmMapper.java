package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FilmMapper implements RowMapper<Film> {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getInt("rate"))
                .mpa(getMpaRating(rs.getInt("mpa_id")))
                .genres(getGenres(rs.getLong("film_id")))
                .build();
        Long directorId = (Long) rs.getLong("directorId");
        if (directorId == null){
            film.setDirector(null);
        }
        film.setDirector(getDirector(directorId));
        return film;
    }
    private Director getDirector(long id) {
        String sql = "SELECT * FROM directors WHERE director_id = ?";
            Director director = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Director(
                    rs.getInt("director_id"),
                    rs.getString("name")
            ), id);
            return director;
    }

    private MpaRating getMpaRating(int id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new MpaRating(
                rs.getInt("mpa_id"),
                rs.getString("name")
        ));
    }

    private List<Genre> getGenres(long filmId) {
        String sql = "SELECT g.genre_id, g.name FROM genres g " +
                "INNER JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        ));
        return genres.stream()
                .distinct()
                .collect(Collectors.toList());
    }
}