package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = FilmorateApplication.class)
@AutoConfigureMockMvc
@Sql(scripts = {"classpath:del_tables.sql", "classpath:schema.sql", "classpath:data.sql"}, executionPhase = BEFORE_TEST_METHOD)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final MockMvc mockMvc;
    private Film film;

    @Autowired
    public FilmControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
        film = Film.builder()
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .mpa(MpaRating.builder().id(1).build())
                .build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostOk() throws Exception {
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testPostError() throws Exception {
        film.setId(1);
        String json = mapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testPutOk() throws Exception {
        film = postFilm(film);

        film.setName("Updated Name");
        String jsonRq = mapper.writeValueAsString(film);
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(jsonRq)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testPutUnknownId() throws Exception {
        postFilm(film);
        film = Film.builder()
                .id(100)
                .name("Film Name")
                .description("Film Description")
                .releaseDate(LocalDate.of(1980, 12, 1))
                .duration(180)
                .mpa(MpaRating.builder().id(1).build())
                .build();
        String jsonRq = mapper.writeValueAsString(film);
        mockMvc.perform(put("/films").contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(jsonRq)
                                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        film = postFilm(film);

        mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(film.getId())))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(film.getName())));
    }

    @Test
    void getById() throws Exception {
        film = postFilm(film);

        mockMvc.perform(get("/films/" + film.getId()).contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(film.getId())))
                .andExpect(jsonPath("$.name", Matchers.equalTo(film.getName())));
    }

    @Test
    public void testSetDeleteLike() throws Exception {
        film = postFilm(film);
        User user = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        user = postUser(user);

        mockMvc.perform(put(String.format("/films/%d/like/%d", film.getId(), user.getId())).contentType(
                        MediaType.APPLICATION_JSON).characterEncoding("utf-8").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(put(String.format("/films/%d/like/%d",
                                          100,
                                          user.getId())).contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

        mockMvc.perform(delete(String.format("/films/%d/like/%d", film.getId(), user.getId())).contentType(
                        MediaType.APPLICATION_JSON).characterEncoding("utf-8").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(delete(String.format("/films/%d/like/%d",
                                             100,
                                             user.getId())).contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    public void testGetTopPopular() throws Exception {

        List<User> users = new ArrayList<>();
        for (int filmNum = 0; filmNum < 12; filmNum++) {
            film = Film.builder()
                    .name("Film Name" + filmNum)
                    .description("Film Description" + filmNum)
                    .releaseDate(LocalDate.of(1980, 12, 1))
                    .duration(180)
                    .mpa(MpaRating.builder().id(1).build())
                    .build();
            User user = User.builder()
                    .login("user_login" + filmNum)
                    .name("User Name" + filmNum)
                    .email("user@mail.ru")
                    .birthday(LocalDate.of(1980, 12, 1))
                    .build();

            film = postFilm(film);
            users.add(postUser(user));

            for (int i = 1; i < users.size(); i++) {
                mockMvc.perform(put(String.format("/films/%d/like/%d", film.getId(), users.get(i).getId())).contentType(
                        MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
            }
        }

        // запрос без количества
        mockMvc.perform(get("/films/popular").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(12)));


        // запрос с количеством
        mockMvc.perform(get("/films/popular").param("count", "5")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(12)));
    }

    private Film postFilm(Film film) throws Exception {
        String jsonRq = mapper.writeValueAsString(film);
        String jsonRs = mockMvc.perform(post("/films").contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(jsonRq)
                                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        film = mapper.readValue(jsonRs, Film.class);
        return film;
    }

    private User postUser(User user) throws Exception {
        String jsonRq = mapper.writeValueAsString(user);
        String jsonRs = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(jsonRq)
                                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);
        return user;
    }
}