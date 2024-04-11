package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilmsWithFriend(@RequestParam long userId, @RequestParam long friendId) {
        log.info("Получение общего списка друзей");
        return filmService.getCommonFilmsWithFriend(userId, friendId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsByGenreAndYear(@RequestParam(defaultValue = "10") int count,
                                                    @RequestParam(defaultValue = "0") Integer genreId,
                                                    @RequestParam(defaultValue = "0") Integer year) {
        if (genreId == 0 && year == 0) {
            log.info("Получение популярных фильмов");
            return filmService.getTopFilms(count);
        }
        log.info("Получение популярных фильмов по жанру или году");
        return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        log.info("Поиск фильма по {} с параметром {} ", by, query);
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable long directorId, @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Вызов списка фильмов");
        return filmService.getStorage();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Получение фильма");
        return filmService.getById(id);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Создание нового фильма");
        return filmService.createFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Добавление лайка");
        filmService.addLike(id, userId);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновление данных фильма");
        return filmService.update(film);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Удаление лайка ");
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@NotNull @PathVariable Long id) {
        log.info("Удаление фильма");
        filmService.delete(id);
    }
}
