package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@NoArgsConstructor
public class Director {
    private Integer id;
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
}
