package ru.yandex.practicum.filmorate.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmConstraintValidator implements ConstraintValidator<MinFilmDate, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(MinFilmDate constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext ct) {
        return value.isBefore(minimumDate) ? false : true;
    }
}
