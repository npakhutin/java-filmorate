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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();
    User user;
    UserController controller;

    @BeforeEach
    void setUp() {
        user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        controller = new UserController();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostOk() throws Exception {
        user.setId(null);
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(user.getName())));
    }

    @Test
    public void testPostError() throws Exception {
        user.setId(1);
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPutOk() throws Exception {
        user.setId(null);
        String jsonRq = mapper.writeValueAsString(user);
        String jsonRs = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);

        user.setName("Updated Name");
        jsonRq = mapper.writeValueAsString(user);
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(user.getName())));
    }

    @Test
    public void testPutUnknownId() throws Exception {
        user.setId(null);
        String jsonRq = mapper.writeValueAsString(user);
        String jsonRs = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);

        user.setId(100);
        jsonRq = mapper.writeValueAsString(user);
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        user.setId(null);
        String jsonRq = mapper.writeValueAsString(user);
        String jsonRs = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(user.getId())))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(user.getName())));
    }
}
