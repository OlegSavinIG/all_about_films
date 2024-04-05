package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getById(int id) {
        return directorStorage.getById(id)
                .orElseThrow(() -> new NotExistException("Не существует режиссер с таким id " + id));
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director update(Director director) {
        directorStorage.getById(director.getId()).orElseThrow(() -> new NotExistException("Не существует режиссер"));
        return directorStorage.update(director);
    }

    public void deleteById(int id) {
        directorStorage.deleteById(id);
    }
}
