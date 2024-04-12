package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;

import static org.junit.jupiter.api.Assertions.*;
@JdbcTest
@ContextConfiguration(classes = {EventDbStorage.class})
class EventDbStorageTest {

    @Test
    void getEventByUserId() {
    }

    @Test
    void saveEvent() {
    }
}