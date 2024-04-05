package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Service
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<MpaRating> findAll() {
        return mpaDbStorage.findAll();
    }

    public MpaRating findById(int id) {
        return mpaDbStorage.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid MPA rating ID: " + id));
    }
}
