package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MpaStorage {
    List<MpaRating> findAll();

    Optional<MpaRating> findById(int id);
}
