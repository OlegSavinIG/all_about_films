package ru.yandex.practicum.filmorate.annotation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilmConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinFilmDate {
    String value() default "1895-12-28";

    String message() default ("Дата указана неправильно");

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

}
