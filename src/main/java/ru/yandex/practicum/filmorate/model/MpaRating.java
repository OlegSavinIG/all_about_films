package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode
@NoArgsConstructor
public class MpaRating {
    private int id;
    private String name;

    public MpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
