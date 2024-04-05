package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> userStorage = new HashMap<>();
    private long generatedId;

    @Override
    public User add(User user) {
        user.setId(++generatedId);
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        userStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        userStorage.remove(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userStorage.values());
    }

    @Override
    public Optional<User> getById(long id) {
        return Optional.ofNullable(userStorage.get(id));
    }
}
