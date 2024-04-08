package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, DirectorStorage directorStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
        this.userStorage = userStorage;
    }

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
        }
    }

    public void deleteLike(long filmId, long userId) {
        if (isFilmExist(filmId) && userStorage.getById(userId) != null) {
            filmStorage.deleteLike(filmId, userId);
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        if (directorStorage.getById(directorId) == null) {
            throw new NotExistException("Этого режиссера не существует");
        }
        if (!sortBy.equals("likes") && !sortBy.equals("year")) {
            throw new ValidationException("Не правильно передан параметр сортировки");
        }
        sortBy = sortBy.equals("likes") ? "rate" : "release_date";
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }
}
