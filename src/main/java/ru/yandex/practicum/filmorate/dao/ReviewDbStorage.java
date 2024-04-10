package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.dao.storage.EventStorage;
import ru.yandex.practicum.filmorate.dao.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.model.Event;
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
    private final EventStorage eventStorage;

    @Override
    public Optional<Review> getReviewById(long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        List<Review> review = jdbcTemplate.query(sql, reviewMapper, id);
        if (review.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(review.stream().findFirst().get());
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return jdbcTemplate.query("SELECT * FROM reviews ORDER BY useful DESC LIMIT ?", reviewMapper, count);
    }

    @Override
    public List<Review> getReviewsByFilmId(long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, reviewMapper, filmId, count);
    }

    @Override
    public Review addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews").usingGeneratedKeyColumns("review_id");
        Map<String, Object> values = new HashMap<>();
        values.put("content", review.getContent());
        values.put("isPositive", review.getIsPositive());
        values.put("useful", review.getUseful());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        long reviewId = (Long) simpleJdbcInsert.executeAndReturnKey(values);
        review.setReviewId(reviewId);
        jdbcTemplate.update("INSERT INTO reviews_films_users (review_id, film_id, user_id) VALUES(?, ?, ?)"
                , reviewId, review.getFilmId(), review.getUserId());
        eventStorage.saveEvent(review.getUserId(), reviewId, Event.EventType.REVIEW, Event.Operation.ADD);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET review_id = ?, content = ?, isPositive = ?";
        jdbcTemplate.update(sql, review.getReviewId(), review.getContent(), review.getIsPositive());
        jdbcTemplate.update("DELETE from reviews_films_users WHERE review_id = ?", review.getReviewId());
        jdbcTemplate.update("INSERT INTO reviews_films_users (review_id, film_id, user_id) VALUES(?, ?, ?)"
                , review.getReviewId(), review.getFilmId(), review.getUserId());
        eventStorage.saveEvent(review.getUserId(), review.getReviewId(), Event.EventType.REVIEW, Event.Operation.UPDATE);
        return getReviewById(review.getReviewId()).get();
    }

    @Override
    public void deleteReviewById(long id) {
        Long userId = jdbcTemplate.queryForObject("SELECT user_id FROM reviews WHERE review_id = ?", Long.class, id);
        eventStorage.saveEvent(userId, id, Event.EventType.REVIEW, Event.Operation.REMOVE);
        jdbcTemplate.update("DELETE FROM reviews_rate WHERE review_id = ?", id);
        jdbcTemplate.update("DELETE FROM reviews_films_users WHERE review_id = ?", id);
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
