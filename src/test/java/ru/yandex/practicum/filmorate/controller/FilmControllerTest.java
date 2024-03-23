package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
       private static final ObjectMapper mapper = new ObjectMapper();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public FilmService filmService() {
            return new FilmService();
        }

        @Bean
        public FilmStorage filmStorage() {
            return new InMemoryFilmStorage();
        }
    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostOk() throws Exception {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testPostError() throws Exception {
        Film film = new Film(1, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPutOk() throws Exception {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film = postFilm(film);

        film.setName("Updated Name");
        String jsonRq = mapper.writeValueAsString(film);
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testPutUnknownId() throws Exception {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        postFilm(film);

        film = new Film(100, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        String jsonRq = mapper.writeValueAsString(film);
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film = postFilm(film);

        mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(film.getId())))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(film.getName())));
    }

    @Test
    void getById() throws Exception {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film = postFilm(film);

        mockMvc.perform(get("/films/" + film.getId()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(film.getId())))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testSetDeleteLike() throws Exception {
        Film film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        film = postFilm(film);

        String jsonRs = mockMvc.perform(put(String.format("/films/%d/like/%d", film.getId(), 1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);
        assertEquals(Set.of(1), film.getUsersLiked());

        mockMvc.perform(put(String.format("/films/%d/like/%d", 100, 1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        jsonRs = mockMvc.perform(delete(String.format("/films/%d/like/%d", film.getId(), 1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);
        assertEquals(0, film.getUsersLiked().size());

        mockMvc.perform(delete(String.format("/films/%d/like/%d", 100, 1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testGetTopPopular() throws Exception {

        for (int filmNum = 0; filmNum < 12; filmNum++) {
            Film film = new Film(null, "Film Name" + filmNum, "Film Description" + filmNum, LocalDate.of(1980, 12, 1), 180);

            film = postFilm(film);
            for (int userId = 1; userId < film.getId(); userId++) {
                mockMvc.perform(put(String.format("/films/%d/like/%d", film.getId(), userId)).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
            }
        }

        // запрос без количества
        mockMvc.perform(get("/films/popular").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(12)));


        // запрос с количеством
        mockMvc.perform(get("/films/popular")
                        .param("count", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(12)));
    }

    private Film postFilm(Film film) throws Exception {
        String jsonRq = mapper.writeValueAsString(film);
        String jsonRs = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);
        return film;
    }
}