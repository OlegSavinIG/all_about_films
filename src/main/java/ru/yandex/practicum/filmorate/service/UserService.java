package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.EventStorage;
import ru.yandex.practicum.filmorate.dao.storage.FilmStorage;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final FilmStorage filmStorage;

    public User createUser(User user) {
        if (user.getName().isBlank() || user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (userStorage.getAllUsers().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new AlreadyExistException("Такой пользователь уже существует");
        }
        return userStorage.add(user);
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers(); //
    }

    public User update(User user) {
        if (userStorage.getById(user.getId()) == null) {
            throw new NotExistException("Такого пользователя не существует");
        }
        return userStorage.update(user);
    }

    public void delete(long id) {
        if (userStorage.getById(id) == null) {
            throw new NotExistException("Такой пользователь не существует");
        }
        userStorage.delete(id);
    }

    public User getById(long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotExistException("Не существует ользователя с таким id " + id));
    }

    public List<Event> getEventByUserId(long id) {
        if (userStorage.getById(id) == null) {
            throw new NotExistException("Такого пользователя не существует");
        }
        return eventStorage.getEventByUserId(id);
    }

    public List<Film> getFilmRecommendations(long userId) {
        if (userStorage.getById(userId) == null) {
            throw new NotExistException("Такого пользователя не существует");
        }
        Long commonUserId = userStorage.getCommonUserByLikes(userId);
        if (commonUserId == null) {
            return Collections.emptyList();
        }
       return filmStorage.getLikedFilmsBYUserId(userId, commonUserId);
    }
}
