package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Review extends BaseUnit {
    private String content;
    @NotNull
    private boolean isPositive;
    private int useful = 0;
    @NotNull
    private long userId;
    @NotNull
    private long filmId;
}
