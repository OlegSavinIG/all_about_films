package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    Map<Long, Film> filmStorage = new HashMap<>();
    private long generatedId;

    @Override
    public Film add(Film film) {
        if (film.getId() != null) {
            return film;
        }
        film.setId(++generatedId);
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {

    }

    @Override
    public void deleteLike(long filmId, long userId) {

    }

    @Override
    public List<Film> getTopFilms(int count) {
        return null;
    }

    @Override
    public Film update(Film film) {
        filmStorage.put(film.getId(), film);
        return film;
    }

    @Override
    public void delete(long id) {
        filmStorage.remove(id);
    }

    @Override
    public List<Film> getStorage() {
        return new ArrayList<>(filmStorage.values());
    }

    @Override
    public Film getById(long id) {
        return filmStorage.get(id);
    }
}
