package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.directorStorage = directorStorage;
    }

    private boolean filmExist(long filmId) {
        return filmStorage.getById(filmId) != null;
    }

    public Film createFilm(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        if (!filmExist(film.getId())) {
            throw new NotExistException(HttpStatus.NOT_FOUND, "Такого фильмa не существует");
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
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(long filmId, long userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getTopFilms(count);
    }

    public List<Film> getFilmsByDirector(long directorId, String sortBy) {
        if (directorStorage.getById(directorId) == null) {
            throw new NotExistException(HttpStatus.BAD_REQUEST,"Такого режиссера не существует");
        }
        if (!sortBy.equals("likes") && !sortBy.equals("year")) {
            throw new ValidationException("Не правильно передан параметр сортировки");
        }
        sortBy = sortBy.equals("likes") ? "rate" : "release_date";
        return filmStorage.getFilmsByDirector(directorId, sortBy);
    }
}
