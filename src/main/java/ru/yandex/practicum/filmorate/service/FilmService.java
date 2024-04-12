package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.*;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final GenreStorage genreStorage;

    public boolean isFilmExist(long filmId) {
        return filmStorage.getById(filmId) != null;
    }

    public Film createFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (!isFilmExist(film.getId())) {
            throw new NotExistException("Этого фильмa не существует");
        }
        return filmStorage.update(film);
    }

    public void delete(long id) {
        filmStorage.delete(id);
    }

    public List<Film> getStorage() {
        return filmStorage.getStorage();
    }

    public Film getById(long id) {
        return filmStorage.getById(id);
    }

    public void addLike(long filmId, long userId) {
        if (isFilmExist(filmId) && userStorage.getById(userId) != null) {
            filmStorage.addLike(filmId, userId);
            eventStorage.saveEvent(userId, filmId, Event.EventType.LIKE, Event.Operation.ADD);
        }
    }

    public void deleteLike(long filmId, long userId) {
        if (isFilmExist(filmId) && userStorage.getById(userId) != null) {
            filmStorage.deleteLike(filmId, userId);
            eventStorage.saveEvent(userId, filmId, Event.EventType.LIKE, Event.Operation.REMOVE);
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        if (directorStorage.getById(directorId).isEmpty()) {
            throw new NotExistException("Этого режиссера не существует");
        }
        if (!sortBy.equals("likes") && !sortBy.equals("year")) {
            throw new ValidationException("Не правильно передан параметр сортировки");
        }
        sortBy = sortBy.equals("likes") ? "rate" : "release_date";
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }

    public List<Film> searchFilms(String query, String by) {
        String[] split = by.split(",");
        if (split.length > 1) {
            return filmStorage.searchFilmsByDirectorAndTitle(query);
        }
        if (by.equals("director")) {
            return filmStorage.serchFilmsByDirector(query);
        } else return filmStorage.searchFilmsByTitle(query);
    }

    public List<Film> getPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        genreStorage.findById(genreId);
        return filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public List<Film> getCommonFilmsWithFriend(long userId, long friendId) {
        if (userStorage.getById(userId) == null || userStorage.getById(friendId) == null) {
            throw new NotExistException("Не существует пользователя");
        }
        return filmStorage.getCommonFilmsWithFriend(userId, friendId);
    }
}
