package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;
    private final String sqlGetAll = "SELECT * FROM films";

    private final String sqlGetById = "SELECT * FROM films WHERE film_id = ?";
    private final String sqlDelete = "DELETE FROM films WHERE film_id = ?";
    private final String sqlUpdate = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            " duration = ?, rate = ?, mpa_id = ? WHERE film_id = ?";

    private final String sqlAddLike = "UPDATE films SET rate = rate + 1 WHERE film_id = ?";

    private final String sqlDeleteLike = "UPDATE films SET rate = rate - 1 WHERE film_id = ?";

    private final String sqlGetTopFilms = "SELECT * from films order by rate desc limit ?";


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, FilmMapper filmRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.filmMapper = filmRowMapper;
    }

    @Override
    public Film add(Film film) {
        if (filmValidator(film)) {
            throw new NotExistException("Проблема с передачей данных фильма");
        }
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", film.getName());
        parameters.put("description", film.getDescription());
        parameters.put("release_date", Date.valueOf(film.getReleaseDate()));
        parameters.put("duration", film.getDuration());
        parameters.put("rate", film.getRate());
        parameters.put("mpa_id", film.getMpa().getId());
        long filmId = (Long) simpleJdbcInsert.executeAndReturnKey(parameters);
        if (!film.getGenres().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sql, filmId, genre.getId());
            }
        }
        if (!film.getDirectors().isEmpty()) {
            String sql = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sql, filmId, director.getId());
            }
        }
        return getById(filmId);
    }

    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        String sql = "SELECT * FROM films " +
                "WHERE film_id IN (SELECT film_id FROM film_directors WHERE director_id = ?) " +
                "ORDER BY " + sortBy;
        if (sortBy.equals("rate")) {
            return jdbcTemplate.query(sql + " DESC", filmMapper, directorId);
        }
        List<Film> films = jdbcTemplate.query(sql, filmMapper, directorId);
        return films;
    }

    @Override
    public List<Film> getStorage() {
        log.info("Получение списка всех фильмов");
        return jdbcTemplate.query(sqlGetAll, filmMapper);
    }

    @Override
    public Film getById(long id) {
        String sqlChecker = "Select film_id from films where film_id = ?";
        List<Long> filmIdId = jdbcTemplate.query(sqlChecker, new Object[]{id}, (rs, rowNum) -> rs.getLong("film_id"));
        if (filmIdId.isEmpty()) {
            throw new NotExistException("Не существует фильма с таким id " + id);
        }
        log.info("Получение фильма по ID: {}", id);
        return jdbcTemplate.queryForObject(sqlGetById, filmMapper, id);
    }

    @Override
    public void delete(long id) {
        log.info("Удаление фильма с ID: {}", id);
        jdbcTemplate.update(sqlDelete, id);
    }

    @Override
    public Film update(Film film) {
        log.info("Начало обновления фильма с ID: {}", film.getId());
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        String sqlForGenres = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : film.getGenres()) {
            log.info("Вставка жанра с ID: {} для фильма с ID: {}", genre.getId(), film.getId());
            jdbcTemplate.update(sqlForGenres, film.getId(), genre.getId());
        }

        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", film.getId());
        String sqlForDirectors = "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";
        for (Director director : film.getDirectors()) {
            log.info("Вставка режиссера с ID: {} для фильма с ID: {}", director.getId(), film.getId());
            jdbcTemplate.update(sqlForDirectors, film.getId(), director.getId());
        }
        log.info("Обновление фильма: {}", film);
        jdbcTemplate.update(sqlUpdate, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        log.info("Фильм с ID: {} успешно обновлен", film.getId());
        return getById(film.getId());
    }

    @Override
    public List<Film> getLikedFilmsBYUserId(long userId, Long commonUserId) {
        List<Film> userFilms = jdbcTemplate.query("SELECT * FROM films WHERE film_id IN " +
                "(SELECT film_id FROM likes WHERE user_id = ?)", filmMapper, userId);
        List<Film> recommendedFilms = jdbcTemplate.query("SELECT * FROM films WHERE film_id IN " +
                "(SELECT film_id FROM likes WHERE user_id = ?)", filmMapper, commonUserId);
        recommendedFilms.removeAll(userFilms);
        return recommendedFilms;
    }

    @Override
    public List<Film> searchFilmsByDirectorAndTitle(String query) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa_id, f.rate FROM films f " +
                "LEFT JOIN film_directors fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors d ON fd.director_id = d.director_id " +
                "WHERE f.name ILIKE ? OR d.name ILIKE ? " +
                "ORDER BY f.rate";
        return jdbcTemplate.query(sql, filmMapper, "%" + query + "%", "%" + query + "%");
    }

    @Override
    public List<Film> serchFilmsByDirector(String query) {
        String sql = "SELECT f.* FROM films f " +
                "JOIN film_directors fd ON f.film_id = fd.film_id " +
                "JOIN directors d ON fd.director_id = d.director_id " +
                "WHERE d.name ILIKE ? " +
                "ORDER BY f.rate";
        return jdbcTemplate.query(sql, filmMapper, "%" + query + "%");
    }

    @Override
    public List<Film> searchFilmsByTitle(String query) {
        String sql = "SELECT * FROM films " +
                "WHERE name ILIKE ? " +
                "ORDER BY rate DESC";
        List<Film> filmByTitle = jdbcTemplate.query(sql, filmMapper, "%" + query + "%");
        return filmByTitle;
    }

    @Override
    public List<Film> getPopularFilmsByGenreAndYear(int count, Integer genreId, Integer year) {
        String sql = "SELECT f.* FROM films f " +
                "LEFT JOIN film_genres fg ON fg.film_id = f.film_id " +
                "%s " +
                "GROUP BY f.film_id " +
                "ORDER BY rate DESC " +
                "LIMIT ?";
        List<String> params = new ArrayList<>();
        if (genreId != 0) {
            params.add("fg.genre_id = " + genreId);
        }
        if (year != 0) {
            params.add("YEAR(f.release_date) = " + year);
        }
        String sqlParams = (!params.isEmpty()) ? "WHERE ".concat(String.join("AND ", params)) : "";
        return jdbcTemplate.query(String.format(sql, sqlParams), filmMapper, count);
    }

    @Override
    public List<Film> getCommonFilmsWithFriend(long userId, long friendId) {
        String sql = "SELECT f.* " +
                "FROM films f " +
                "JOIN likes l ON f.film_id = l.film_id " +
                "JOIN likes l2 ON f.film_id = l2.film_id " +
                "WHERE l.user_id = ? AND l2.user_id = ? " +
                "ORDER BY f.rate DESC ";
        return jdbcTemplate.query(sql, filmMapper, userId, friendId);

    }

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update(sqlAddLike, filmId);
        String sqlLikesTable = "INSERT into likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlLikesTable, filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        jdbcTemplate.update(sqlDeleteLike, filmId);
        String sqlLikesTable = "DELETE from likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlLikesTable, filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        List<Film> topFilms = jdbcTemplate.query(sqlGetTopFilms, filmMapper, count);
        return topFilms;
    }

    private boolean filmValidator(Film film) {
        if (film == null) {
            return true;
        }
        MpaRating mpa = film.getMpa();
//        if (mpa == null) {
//            return true;
//        }
        if (mpa.getId() > 5) {
            return true;
        }
        return false;

//        String sqlChecker = "Select mpa_id from mpa where mpa_id = ?";
//        List<Integer> mpaId = jdbcTemplate.query(sqlChecker, new Object[]{film.getMpa().getId()}, (rs, rowNum) -> rs.getInt("mpa_id"));
//        if (mpaId.isEmpty()) {
//            return true;
//        }
//        String sqlGenreChecker = "Select genre_id from genres where genre_id = ?";
//        List<Integer> genreIds = film.getGenres().stream()
//                .map(Genre::getId)
//                .collect(Collectors.toList());
//
//        for (Integer genreId : genreIds) {
//            List<Integer> result = jdbcTemplate.query(sqlGenreChecker, new Object[]{genreId}, (rs, rowNum) -> rs.getInt("genre_id"));
//            if (result.isEmpty()) {
//                return true;
//            }
//        }
//        return false;
    }
}