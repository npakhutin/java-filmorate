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
    @Autowired
    private MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public UserService userService() {
            return new UserService();
        }

        @Bean
        public UserStorage userStorage() {
            return new InMemoryUserStorage();
        }

    }

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testPostOk() throws Exception {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.notNullValue()))
                .andExpect(jsonPath("$.name", Matchers.equalTo(user.getName())));
    }

    @Test
    public void testPostError() throws Exception {
        User user = new User(1, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        String json = mapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testPutOk() throws Exception {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
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
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        postUser(user);

        user = new User(100, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        String jsonRq = mapper.writeValueAsString(user);
        mockMvc.perform(put("/users").contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .content(jsonRq).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll() throws Exception {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
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
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        user = postUser(user);

        mockMvc.perform(get("/users/" + user.getId()).contentType(MediaType.APPLICATION_JSON).characterEncoding("utf-8")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.equalTo(user.getId())))
                .andExpect(jsonPath("$.name", Matchers.equalTo(user.getName())));
    }

    @Test
    void testAddDeleteFriend() throws Exception  {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        User friend = new User(null, "friend_login", "Friend Name", "friend@mail.ru", LocalDate.of(1980, 12, 1));

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
    void testAddDeleteUnknownFriend() throws Exception  {
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));

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
        User user = new User(null, "user_login", "User Name", "user@mail.ru", LocalDate.of(1980, 12, 1));
        User friend1 = new User(null, "friend1_login", "Friend1 Name", "friend1@mail.ru", LocalDate.of(1980, 12, 1));
        User friend2 = new User(null, "friend2_login", "Friend2 Name", "friend2@mail.ru", LocalDate.of(1980, 12, 1));

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
        User user1 = new User(null, "user1_login", "User1 Name", "user1@mail.ru", LocalDate.of(1980, 12, 1));
        User user2 = new User(null, "user2_login", "User2 Name", "user2@mail.ru", LocalDate.of(1980, 12, 1));
        User friend1 = new User(null, "friend1_login", "Friend1 Name", "friend1@mail.ru", LocalDate.of(1980, 12, 1));
        User friend2 = new User(null, "friend2_login", "Friend2 Name", "friend2@mail.ru", LocalDate.of(1980, 12, 1));

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
}
