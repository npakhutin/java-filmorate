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
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final ObjectMapper mapper = new ObjectMapper();
    private final MockMvc mockMvc;
    private User user;

    @Autowired
    public UserControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    void setUp() {
        user = User.builder()
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostOk() throws Exception {
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
        user = postUser(user);

        user.setName("Updated Name");
        String jsonRq = mapper.writeValueAsString(user);
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(user.getName())));
    }

    @Test
    public void testPutUnknownId() throws Exception {
        postUser(user);

        user = User.builder()
                .id(100)
                .login("user_login")
                .name("User Name")
                .email("user@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

        String jsonRq = mapper.writeValueAsString(user);
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        user = postUser(user);

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", Matchers.equalTo(user.getId())))
                .andExpect(jsonPath("$[0].name", Matchers.equalTo(user.getName())));
    }

    @Test
    void getById() throws Exception {
        user = postUser(user);

        mockMvc.perform(get("/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(user.getId())))
                .andExpect(jsonPath("$.name", Matchers.equalTo(user.getName())));
    }

    @Test
    void testAddDeleteFriend() throws Exception {
        User friend = User.builder()
                .login("friend_login")
                .name("Friend Name")
                .email("friend@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

        user = postUser(user);
        friend = postUser(friend);

        String jsonRs = mockMvc.perform(put(String.format("/users/%d/friends/%d", user.getId(), friend.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);

        assertEquals(List.of(friend.getId()), user.getFriendIds());

        jsonRs = mockMvc.perform(delete(String.format("/users/%d/friends/%d", user.getId(), friend.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);

        assertEquals(0, user.getFriendIds().size());
    }

    @Test
    void testAddDeleteUnknownFriend() throws Exception {

        user = postUser(user);

        mockMvc.perform(put(String.format("/users/%d/friends/%d", user.getId(), -1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete(String.format("/users/%d/friends/%d", user.getId(), -1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getFriends() throws Exception {
        User friend1 = User.builder()
                .login("friend1_login")
                .name("Friend1 Name")
                .email("friend1@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        User friend2 = User.builder()
                .login("friend2_login")
                .name("Friend2 Name")
                .email("friend2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

        user = postUser(user);
        friend1 = postUser(friend1);
        friend2 = postUser(friend2);

        mockMvc.perform(put(String.format("/users/%d/friends/%d", user.getId(), friend1.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(put(String.format("/users/%d/friends/%d", user.getId(), friend2.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get(String.format("/users/%d/friends", user.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get(String.format("/users/%d/friends", -1)).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommonFriends() throws Exception {

        User user1 = User.builder()
                .login("user1_login")
                .name("User1 Name")
                .email("user1@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        User user2 = User.builder()
                .login("user2_login")
                .name("User2 Name")
                .email("user2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        User friend1 = User.builder()
                .login("friend1_login")
                .name("Friend1 Name")
                .email("friend1@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();
        User friend2 = User.builder()
                .login("friend2_login")
                .name("Friend2 Name")
                .email("friend2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 1))
                .build();

        user1 = postUser(user1);
        user2 = postUser(user2);
        friend1 = postUser(friend1);
        friend2 = postUser(friend2);

        // добавляем друзей
        mockMvc.perform(put(String.format("/users/%d/friends/%d", user1.getId(), friend1.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(put(String.format("/users/%d/friends/%d", user1.getId(), friend2.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(put(String.format("/users/%d/friends/%d", user2.getId(), friend1.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(put(String.format("/users/%d/friends/%d", user2.getId(), friend2.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // загружаем обновленных друзей с сервера
        String jsonRs;
        jsonRs = mockMvc.perform(get("/users/" + friend1.getId()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        mapper.readValue(jsonRs, User.class);

        jsonRs = mockMvc.perform(get("/users/" + friend2.getId()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        mapper.readValue(jsonRs, User.class);

        mockMvc.perform(get(String.format("/users/%d/friends/common/%d", user1.getId(), user2.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get(String.format("/users/%d/friends/common/%d", user1.getId() + 10, user2.getId())).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private User postUser(User user) throws Exception {
        String jsonRq = mapper.writeValueAsString(user);
        String jsonRs = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        user = mapper.readValue(jsonRs, User.class);
        return user;
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public UserService userService() {
            return new UserService(userStorage());
        }

        @Bean
        public UserStorage userStorage() {
            return new InMemoryUserStorage();
        }
    }
}
