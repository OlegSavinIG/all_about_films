package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.dao.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewMapper reviewMapper;

    @Override
    public Optional<Review> getReviewById(long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> review = jdbcTemplate.query(sql, reviewMapper, id);
        return Optional.ofNullable(review.stream().findFirst().get());
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return jdbcTemplate.query("SELECT * FROM reviews LIMIT ?", reviewMapper, count);
    }

    @Override
    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? LIMIT ?";
        return jdbcTemplate.query(sql, reviewMapper, filmId, count);
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews").usingGeneratedKeyColumns("review_id");
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("isPositive", review.isPositive());
        values.put("useful", review.getUseful());
        long reviewId = (Long) simpleJdbcInsert.executeAndReturnKey(values);
        jdbcTemplate.update("INSERT INTO reviews_films_users (review_id, film_id, user_id) VALUES(?, ?, ?)"
                , reviewId, review.getFilmId(), review.getUserId());
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET review_id = ?, content = ?, useful = ?, isPositive = ?";
        jdbcTemplate.update(sql, review.getId(), review.getContent(), review.getUseful(), review.isPositive());
        jdbcTemplate.update("DELETE from reviews_films_users WHERE review_id = ?", review.getId());
        jdbcTemplate.update("INSERT INTO reviews_films_users (review_id, film_id, user_id) VALUES(?, ?, ?)"
                , review.getId(), review.getFilmId(), review.getUserId());
        return getReviewById(review.getId()).get();
    }

    @Override
    public void deleteReviewById(long id) {
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", id);
    }

    @Override
    public void addReviewLike(long id, long userId) {
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", id);
        jdbcTemplate.update("INSERT INTO reviews_rate (review_id, user_id) VALUES(?, ?)", id, userId);
    }

    @Override
    public void addReviewDislike(long id, long userId) {
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", id);
        jdbcTemplate.update("INSERT INTO reviews_rate (review_id, user_id) VALUES(?, ?)", id, userId);
    }

    @Override
    public void deleteReviewLike(long id, long userId) {
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review = ?", id);
        jdbcTemplate.update("DELETE FROM reviews_rate WHERE review_id = ?", id);
    }

    @Override
    public void deleteReviewDislike(long id, long userId) {
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review = ?", id);
        jdbcTemplate.update("DELETE FROM reviews_rate WHERE review_id = ?", id);
    }
}
