package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ContextConfiguration(classes = {FilmDbStorage.class, FilmMapper.class})
public class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmStorage;
    private Film testFilm;

    @BeforeEach
    public void setUp() {
        testFilm = Film.builder()
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(120)
                .rate(5)
                .mpa(new MpaRating(1, "G"))
                .build();
        testFilm = filmStorage.add(testFilm);
    }

    @Test
    public void testAddAndGetFilmById() {
        Film fetchedFilm = filmStorage.getById(testFilm.getId());

        assertThat(fetchedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    public void testUpdateFilm() {
        Film savedFilm = filmStorage.add(testFilm);
        savedFilm.setName("Updated Name");
        savedFilm.setDescription("Updated Description");
        savedFilm.setDuration(150);
        Film updatedFilm = filmStorage.update(savedFilm);

        assertThat(updatedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(savedFilm);
    }

    @Test
    public void testDeleteFilm() {
        filmStorage.delete(testFilm.getId());
        List<Film> filmsAfterDeletion = filmStorage.getStorage();

        assertThat(filmsAfterDeletion)
                .isNotNull()
                .doesNotContain(testFilm);
    }

    @Test
    public void testGetTopFilms() {
        Film film2 = Film.builder()
                .name("Film Two")
                .description("Description Two")
                .releaseDate(LocalDate.of(2001, 2, 2))
                .duration(150)
                .rate(7)
                .mpa(new MpaRating(2, "PG"))
                .build();
        film2.getGenres().addAll(new HashSet<>());
        filmStorage.add(film2);
        List<Film> topFilms = filmStorage.getTopFilms(2);

        assertThat(topFilms)
                .isNotNull()
                .hasSize(2)
                .contains(film2);
    }
}