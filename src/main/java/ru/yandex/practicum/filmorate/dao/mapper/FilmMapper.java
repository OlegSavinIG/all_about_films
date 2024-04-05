package ru.yandex.practicum.filmorate.dao.mapper;

import org.springframework.beans.factory.annotation.Autowired;
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
                .build();
        Long filmId = rs.getLong("film_id");
            film.getDirectors().addAll(getDirector(filmId));
            film.getGenres().addAll(getGenres(filmId));
        return film;
    }

    private List<Director> getDirector(long filmId) {
        String sql = "SELECT d.director_id , d.name " +
                "FROM film_directors fd " +
                "JOIN directors d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id = ?";
        List<Director> directors = jdbcTemplate.query(sql, (rs, rowNum) -> new Director(
                rs.getInt("director_id"),
                rs.getString("name")
        ), filmId);
        return directors;
    }

    private MpaRating getMpaRating(int id) {
        String sql = "SELECT * FROM mpa WHERE mpa_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) -> new MpaRating(
                rs.getInt("mpa_id"),
                rs.getString("name")
        ));
    }

    private List<Genre> getGenres(long filmId) {
        String sql = "SELECT distinct g.genre_id, g.name " +
                "FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(sql, new Object[]{filmId}, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        ));
//        return genres.stream()
//                .distinct()
//                .collect(Collectors.toList());
        return genres;
    }
}