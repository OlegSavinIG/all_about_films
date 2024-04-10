package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private long userId;
    private EventType eventType;
    private Operation operation;
    private long eventId;
    private long entityId;
    private Long timestamp;
    public enum EventType {
        LIKE, REVIEW, FRIEND
    }
    public enum Operation {
        REMOVE, ADD, UPDATE
    }
}
