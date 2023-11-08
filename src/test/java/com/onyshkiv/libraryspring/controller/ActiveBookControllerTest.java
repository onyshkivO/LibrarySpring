package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.*;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.service.ActiveBookService;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.util.ActiveBookValidator;
import com.onyshkiv.libraryspring.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.Errors;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActiveBookController.class)
@AutoConfigureMockMvc(addFilters = false)

public class ActiveBookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ActiveBookService activeBookService;
    @MockBean
    private ActiveBookValidator activeBookValidator;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private MyUserDetailsService myUserDetailsService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getActiveBookByIdTest() throws Exception {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .dateOfPublication(LocalDate.of(2023, 8, 12))
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .user(user)
                .book(book)
                .startDate(LocalDate.of(2021, 8, 12))
                .endDate(LocalDate.of(2021, 9, 12))
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        when(activeBookService.getActiveBookById(anyInt()))
                .thenReturn(activeBook);

        mockMvc.perform(get("/activeBooks/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.startDate").value(LocalDate.of(2021, 8, 12).toString()))
                .andExpect(jsonPath("$.endDate").value(LocalDate.of(2021, 9, 12).toString()))
                .andExpect(jsonPath("$.fine").value(100.00))
                .andExpect(jsonPath("$.user.login").value("user"))
                .andExpect(jsonPath("$.book.isbn").value("123"))
                .andExpect(jsonPath("$.subscriptionStatus").value(SubscriptionStatus.ACTIVE.toString()));
        verify(activeBookService, times(1)).getActiveBookById(anyInt());
    }

    @Test
    public void getActiveBooksTest() throws Exception {
        ActiveBook activeBook1 = ActiveBook.builder()
                .id(1)
                .startDate(LocalDate.of(2021, 8, 12))
                .endDate(LocalDate.of(2021, 9, 12))
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        ActiveBook activeBook2 = ActiveBook.builder()
                .id(2)
                .startDate(LocalDate.of(2022, 8, 12))
                .endDate(LocalDate.of(2022, 9, 12))
                .fine(200.00)
                .subscriptionStatus(SubscriptionStatus.RETURNED)
                .build();
        ActiveBook activeBook3 = ActiveBook.builder()
                .id(3)
                .startDate(LocalDate.of(2023, 8, 12))
                .endDate(LocalDate.of(2023, 9, 12))
                .fine(300.00)
                .subscriptionStatus(SubscriptionStatus.FINED)
                .build();
        List<ActiveBook> activeBooksList = List.of(
                activeBook1,
                activeBook2,
                activeBook3
        );

        when(activeBookService.getAllActiveBooks(any()))
                .thenReturn(new DataPageDto<>(activeBooksList, 0, 1));

        mockMvc.perform(get("/activeBooks")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].startDate").value(LocalDate.of(2021, 8, 12).toString()))
                .andExpect(jsonPath("$.data[0].fine").value(100.00))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].startDate").value(LocalDate.of(2022, 8, 12).toString()))
                .andExpect(jsonPath("$.data[1].fine").value(200.00))
                .andExpect(jsonPath("$.data[2].id").value(3))
                .andExpect(jsonPath("$.data[2].startDate").value(LocalDate.of(2023, 8, 12).toString()))
                .andExpect(jsonPath("$.data[2].fine").value(300.00));
        verify(activeBookService, times(1)).getAllActiveBooks(any());
    }

    @Test
    public void createActiveBookWithoutErrorsTest() throws Exception {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .user(user)
                .book(book)
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        String activeBookJson = objectMapper.writeValueAsString(activeBook);

        when(activeBookService.saveActiveBook(any()))
                .thenReturn(activeBook);

        mockMvc.perform(post("/activeBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeBookJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fine").value(100.00))
                .andExpect(jsonPath("$.user.login").value("user"))
                .andExpect(jsonPath("$.book.isbn").value("123"))
                .andExpect(jsonPath("$.subscriptionStatus").value(SubscriptionStatus.ACTIVE.toString()));
        verify(activeBookService, times(1)).saveActiveBook(any());
    }


    @Test
    public void createActiveBookWithErrorsTest() throws Exception {
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .fine(100.00)
                .build();
        String activeBookJson = objectMapper.writeValueAsString(activeBook);
        mockMvc.perform(post("/activeBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeBookJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ActiveBookNotSavedException));
        verifyNoInteractions(activeBookService);
    }

    @Test
    public void saveActiveBookWithErrorsByUserValidatorTest() throws Exception {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .quantity(0)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .user(user)
                .book(book)
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        String activeBookJson = objectMapper.writeValueAsString(activeBook);
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("book", "", "There are not available book with isbn " + book.getIsbn());

            return null;
        }).when(activeBookValidator).validate(any(), any());
        mockMvc.perform(post("/activeBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeBookJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof ActiveBookNotSavedException))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException().getMessage().contains("There are not available book with isbn " + book.getIsbn())));
        verifyNoInteractions(activeBookService);
        verify(activeBookValidator, times(1)).validate(any(), any());
    }

    @Test
    public void updateActiveBookTest() throws Exception {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .user(user)
                .book(book)
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        String activeBookJson = objectMapper.writeValueAsString(activeBook);


        when(activeBookService.updateActiveBook(anyInt(), any()))
                .thenReturn(activeBook);

        mockMvc.perform(put("/activeBooks/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fine").value(100.00))
                .andExpect(jsonPath("$.user.login").value("user"))
                .andExpect(jsonPath("$.book.isbn").value("123"))
                .andExpect(jsonPath("$.subscriptionStatus").value(SubscriptionStatus.ACTIVE.toString()));
        verify(activeBookService, times(1)).updateActiveBook(anyInt(), any());
    }


    @Test
    public void returnActiveBookTest() throws Exception {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .user(user)
                .book(book)
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        String activeBookJson = objectMapper.writeValueAsString(activeBook);


        when(activeBookService.returnActiveBook(anyInt()))
                .thenReturn(activeBook);

        mockMvc.perform(put("/activeBooks/return/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(activeBookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fine").value(100.00))
                .andExpect(jsonPath("$.user.login").value("user"))
                .andExpect(jsonPath("$.book.isbn").value("123"))
                .andExpect(jsonPath("$.subscriptionStatus").value(SubscriptionStatus.ACTIVE.toString()));
        verify(activeBookService, times(1)).returnActiveBook(anyInt());
    }


    @Test
    public void deleteActiveBookTest() throws Exception {
        mockMvc.perform(delete("/activeBooks/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(activeBookService, times(1)).delete(anyInt());
    }

}
