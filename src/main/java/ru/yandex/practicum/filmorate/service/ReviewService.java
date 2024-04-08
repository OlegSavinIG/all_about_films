package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;

    public Review getReviewById(long id) {
        return reviewStorage.getReviewById(id).orElseThrow(() -> new NotExistException("Не существует пользователя с таким id"));
    }

    public List<Review> getAllReviews(int count) {
        return reviewStorage.getAllReviews(count);
    }

    public List<Review> getReviewsByFilmId(Long filmId, int count) {
        if (!filmService.isFilmExist(filmId)) {
            throw new NotExistException("Не существует фильма с таким id");
        }
        return reviewStorage.getReviewsByFilmId(filmId, count);
    }

    public Review addReview(Review review) {
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        if (!isReviewExist(review.getId())) {
            throw new NotExistException("Этот отзыв не существует");
        }
        return reviewStorage.updateReview(review);
    }

    public void deleteReviewById(long id) {
        if (!isReviewExist(id)) {
            throw new NotExistException("Этот отзыв не существует");
        }
        reviewStorage.deleteReviewById(id);
    }

    public void addReviewLike(long id, long userId) {
        userService.getById(userId);
        if (!isReviewExist(id)) {
            throw new NotExistException("Этот отзыв не существует");
        }
        reviewStorage.addReviewLike(id, userId);
    }

    public void addReviewDislike(long id, long userId) {
        userService.getById(userId);
        if (!isReviewExist(id)) {
            throw new NotExistException("Этот отзыв не существует");
        }
        reviewStorage.addReviewDislike(id, userId);
    }

    public void deleteReviewLike(long id, long userId) {
        userService.getById(userId);
        if (!isReviewExist(id)) {
            throw new NotExistException("Этот отзыв не существует");
        }
        reviewStorage.deleteReviewLike(id, userId);
    }

    public void deleteReviewDislike(long id, long userId) {
        userService.getById(userId);
        if (!isReviewExist(id)) {
            throw new NotExistException("Этот отзыв не существует");
        }
        reviewStorage.deleteReviewDislike(id, userId);
    }

    private boolean isReviewExist(long id) {
        return reviewStorage.getReviewById(id) != null;
    }
}
