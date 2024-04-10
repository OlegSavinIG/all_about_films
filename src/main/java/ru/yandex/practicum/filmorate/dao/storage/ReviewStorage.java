package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Optional<Review> getReviewById(long id);

    List<Review> getAllReviews(int count);

    List<Review> getReviewsByFilmId(long filmId, int count);

    Review addReview(Review review);

    Review updateReview(Review review);

    void deleteReviewById(long id);

    void addReviewLike(long id, long userId);

    void addReviewDislike(long id, long userId);

    void deleteReviewLike(long id, long userId);

    void deleteReviewDislike(long id, long userId);
}
