package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.dao.storage.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DirectorService {
    private final DirectorStorage directorStorage;

    public List<Director> getAllDirectors() {
        return directorStorage.getAllDirectors();
    }

    public Director getById(int id) {
        return Optional.ofNullable(directorStorage.getById(id).get())
                .orElseThrow(() -> new ValidationException("Не существует пользователь с таким id " + id));
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director update(Director director) {
        if (directorStorage.getById(director.getId()) == null) {
            throw new ValidationException("Такого режиссера не существует");
        }
        return directorStorage.update(director);
    }

    public void deleteById(int id) {
        directorStorage.deleteById(id);
    }
}
