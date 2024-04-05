package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Optional<Director> getById(long id);

    Director createDirector(Director director);

    Director update(Director director);

    void deleteById(int id);
}
