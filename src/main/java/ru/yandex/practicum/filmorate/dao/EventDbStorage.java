package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.storage.EventStorage;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Event> eventRowMapper = (rs, rowNum) -> Event.builder()
            .eventId(rs.getLong("event_id"))
            .userId(rs.getLong("user_id"))
            .entityId(rs.getLong("entity_id"))
            .timestamp(rs.getLong("timestamp"))
            .eventType(Event.EventType.valueOf(rs.getString("event_type")))
            .operation(Event.Operation.valueOf(rs.getString("operation")))
            .build();

    @Override
    public List<Event> getEventByUserId(long id) {
        String sql = "SELECT * FROM events WHERE user_id = ?";
        List<Event> feeds = jdbcTemplate.query(sql, eventRowMapper, id);

        return feeds;
    }

    @Override
    public List<Event> saveEvent(long userId, long entityId, Event.EventType eventType, Event.Operation operation) {
        String sql = "INSERT INTO events (user_id, entity_id, event_type, operation, timestamp) VALUES(?, ?, ?, ?, ?)";
        long timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        jdbcTemplate.update(sql, userId, entityId, eventType.name(), operation.name(), timestamp);
        return getEventByUserId(userId);
    }
}
