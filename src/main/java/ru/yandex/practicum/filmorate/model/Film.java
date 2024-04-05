package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.annotation.MinFilmDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Film extends BaseUnit {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @MinFilmDate
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    private long rate = 0;
    @NotNull
    private MpaRating mpa;
    private final List<Genre> genres = new ArrayList<>();
    private final Set<Integer> likes = new HashSet<>();
    private final List<Director> directors = new ArrayList<>();
}
