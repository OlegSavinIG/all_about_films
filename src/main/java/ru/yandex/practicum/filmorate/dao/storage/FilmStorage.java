package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    void delete(long id);

    List<Film> getStorage();

    Film getById(long id);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    List<Film> getTopFilms(int count);

    List<Film> getFilmsByDirector(long directorId, String sortBy);

    List<Film> getLikedFilmsBYUserId(long userId, Long commonUserId);

    List<Film> searchFilmsByDirectorAndTitle(String query);

    List<Film> serchFilmsByDirector(String query);

    List<Film> searchFilmsByTitle(String query);
}
