package ru.yandex.practicum.filmorate.dao.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    List<Event> getEventByUserId(long id);

    List<Event> saveEvent(long userId, long entityId, Event.EventType eventType, Event.Operation operation);
}
