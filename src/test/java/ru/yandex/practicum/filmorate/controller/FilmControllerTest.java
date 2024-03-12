package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static ObjectMapper mapper = new ObjectMapper();
    Film film;
    FilmController controller;

    @BeforeEach
    void setUp() {
        film = new Film(null, "Film Name", "Film Description", LocalDate.of(1980, 12, 1), 180);
        controller = new FilmController();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostOk() throws Exception {
        film.setId(null);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testPostError() throws Exception {
        film.setId(1);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPutOk() throws Exception {
        film.setId(null);
        String jsonRq = mapper.writeValueAsString(film);
        String jsonRs = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);

        film.setName("Updated Name");
        jsonRq = mapper.writeValueAsString(film);
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testPutUnknownId() throws Exception {
        film.setId(null);
        String jsonRq = mapper.writeValueAsString(film);
        String jsonRs = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);

        film.setId(100);
        jsonRq = mapper.writeValueAsString(film);
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        film.setId(null);
        String jsonRq = mapper.writeValueAsString(film);
        String jsonRs = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);

        mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(film.getId())))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(film.getName())));
    }
}