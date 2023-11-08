package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.user.UserNotSavedException;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.service.UserService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import com.onyshkiv.libraryspring.util.UserValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private UserValidator userValidator;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private MyUserDetailsService myUserDetailsService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getUserByLoginTest() throws Exception {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        when(userService.getUserByLogin(anyString()))
                .thenReturn(user);

        mockMvc.perform(get("/users/{login}", "user")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(userService, times(1)).getUserByLogin(anyString());
    }

    @Test
    public void getUsersTest() throws Exception {
        User user1 = User.builder()
                .login("user1")
                .password("111")
                .build();
        User user2 = User.builder()
                .login("user2")
                .password("222")
                .build();
        User user3 = User.builder()
                .login("user3")
                .password("333")
                .build();
        List<User> usersList = List.of(
                user1,
                user2,
                user3
        );
        when(userService.getAllUsers(any()))
                .thenReturn(new DataPageDto<>(usersList, 0, 1));

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].login").value("user1"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist())
                .andExpect(jsonPath("$.data[1].login").value("user2"))
                .andExpect(jsonPath("$.data[1].password").doesNotExist())
                .andExpect(jsonPath("$.data[2].login").value("user3"))
                .andExpect(jsonPath("$.data[2].password").doesNotExist());

        verify(userService, times(1)).getAllUsers(any());
    }


    @Test
    public void saveUserWithoutErrorsTest() throws Exception {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .firstName("fname")
                .lastName("lname")
                .phone("0981111111")
                .password("1234567")
                .build();
        String jsonUser = objectMapper.writeValueAsString(user);

        when(userService.saveUser(any(), any()))
                .thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("fname"))
                .andExpect(jsonPath("$.lastName").value("lname"))
                .andExpect(jsonPath("$.phone").value("0981111111"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(userService, times(1)).saveUser(any(), any());
    }

    @Test
    public void saveUserWithErrorsTest() throws Exception {
        User user = User.builder()
                .login("")
                .build();
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof UserNotSavedException));
        verifyNoInteractions(userService);
    }

    @Test
    public void saveUserWithErrorsByUserValidatorTest() throws Exception {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .firstName("fname")
                .lastName("lname")
                .phone("0981111111")
                .password("1234567")
                .build();
        String userJson = objectMapper.writeValueAsString(user);
        doAnswer(invocation -> {
            Errors arg1 = invocation.getArgument(1);
            arg1.rejectValue("login", "", "User login already exist");
            return null;
        }).when(userValidator).validate(any(), any());
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof UserNotSavedException))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException().getMessage().contains("User login already exist") ));
        verifyNoInteractions(userService);
        verify(userValidator,times(1)).validate(any(),any());


    }

    @Test
    public void updateUserTest() throws Exception {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .firstName("fname")
                .lastName("lname")
                .phone("0981111111")
                .password("1234567")
                .build();
        String jsonUser = objectMapper.writeValueAsString(user);


        when(userService.updateUser(anyString(), any()))
                .thenReturn(user);

        mockMvc.perform(put("/users/{login}", "user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("fname"))
                .andExpect(jsonPath("$.lastName").value("lname"))
                .andExpect(jsonPath("$.phone").value("0981111111"))
                .andExpect(jsonPath("$.password").doesNotExist());
        verify(userService, times(1)).updateUser(anyString(), any());
    }

    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/{login}", "user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyString());
    }

    @Test
    public void changeUserStatusTest() throws Exception {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .firstName("fname")
                .lastName("lname")
                .phone("0981111111")
                .password("1234567")
                .build();
        when(userService.changeUserStatus(anyString())).thenReturn(user);

        mockMvc.perform(put("/users/status/{login}", "user")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("user"))
                .andExpect(jsonPath("$.email").value("user@gmail.com"))
                .andExpect(jsonPath("$.firstName").value("fname"))
                .andExpect(jsonPath("$.lastName").value("lname"))
                .andExpect(jsonPath("$.phone").value("0981111111"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService, times(1)).changeUserStatus(anyString());
    }

}
