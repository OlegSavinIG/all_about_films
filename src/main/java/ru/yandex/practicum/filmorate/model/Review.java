package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Review {
    private long reviewId;
    @NotEmpty
    private String content;
    @NotNull
    private Boolean isPositive;
    private int useful = 0;
    @NotNull
    @Min(1)
    private Long userId;
    @NotNull
    @Min(1)
    private Long filmId;
}
