package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@ContextConfiguration(classes = {GenreDbStorage.class})
class GenreDbStorageTest {

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }
}