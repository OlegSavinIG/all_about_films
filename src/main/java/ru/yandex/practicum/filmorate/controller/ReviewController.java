package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable long id) {
        log.info("Получение отзыва");
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<Review> getReviewsByFilmIdOrWithout(@RequestParam(defaultValue = "-1") long filmId, @RequestParam(defaultValue = "10") int count) {
        if (filmId == -1) {
            log.info("Получение всех отзывов");
            return reviewService.getAllReviews(count);
        }
        log.info("Получение всех отзывов по фильму с id{}", filmId);
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("Создание отзыва");
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("Обновление отзыва");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("{id}")
    public void deleteReviewById(@PathVariable long id) {
        log.info("Удаление отзыва по id{}", id);
        reviewService.deleteReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addReviewLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Добавление лайка отзыву с id {} от пользователя с id {}", id, userId);
        reviewService.addReviewLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addReviewDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Добавление дизлайка отзыву с id {} от пользователя с id {}", id, userId);
        reviewService.addReviewDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Удаление лайка отзыву с id {} от пользователя с id {}", id, userId);
        reviewService.deleteReviewLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable long id, @PathVariable long userId) {
        log.info("Удаление дизлайка отзыву с id {} от пользователя с id {}", id, userId);
        reviewService.deleteReviewDislike(id, userId);
    }
}
