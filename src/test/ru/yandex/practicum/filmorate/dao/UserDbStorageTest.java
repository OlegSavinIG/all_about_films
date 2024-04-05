package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dao.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ContextConfiguration(classes = {UserDbStorage.class, UserMapper.class})
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userStorage;
    private User testUser;

    @BeforeEach
    public void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .login("testUser")
                .name("Test User")
                .birthday(LocalDate.of(1980, 1, 1))
                .friendList(new ArrayList<>())
                .build();

        testUser = userStorage.add(testUser);
    }

    @Test
    public void testAddAndGetUser() {
        User foundUser = userStorage.getById(testUser.getId()).orElse(null);

        assertThat(foundUser).isNotNull();
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(testUser);
    }

    @Test
    public void testUpdateUser() {
        testUser.setName("Updated Name");
        User updatedUser = userStorage.update(testUser);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    public void testDeleteUser() {
        userStorage.delete(testUser.getId());
        List<User> allUsers = userStorage.getAllUsers();

        assertThat(allUsers).isEmpty();
    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.getAllUsers();

        assertThat(users).isNotNull();
        assertThat(users).contains(testUser);
    }
}
