package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.NotExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;


    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

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
}
