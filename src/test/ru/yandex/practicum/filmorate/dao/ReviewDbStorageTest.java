package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dao.mapper.ReviewMapper;

import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@ContextConfiguration(classes = {ReviewDbStorage.class, ReviewMapper.class})
class ReviewDbStorageTest {

    @Test
    void getReviewById() {
    }

    @Test
    void getAllReviews() {
    }

    @Test
    void getReviewsByFilmId() {
    }

    @Test
    void addReview() {
    }

    @Test
    void updateReview() {
    }

    @Test
    void deleteReviewById() {
    }

    @Test
    void addReviewLike() {
    }

    @Test
    void addReviewDislike() {
    }

    @Test
    void deleteReviewLike() {
    }

    @Test
    void deleteReviewDislike() {
    }
}