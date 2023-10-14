package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.DTO.BookDTO;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.service.BookService;
import com.onyshkiv.libraryspring.util.BookValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@Import(BookValidator.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void bookController_getBooksByIsbnTest() throws Exception {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.of(book));

        mockMvc.perform(get("/books/{id}", "9780312850098")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780312850098"))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    public void bookController_getBooksByIsbnThatNotExistTest() throws Exception {
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/{id}", "9780312850098")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BookNotFoundException))
                .andExpect(result -> Assertions.assertEquals("Not book with isbn 9780312850098", result.getResolvedException().getMessage()));
    }

    @Test
    public void bookController_getAllBooks() throws Exception {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        Book book2 = Book.builder().isbn("9780312850045").name("test2").dateOfPublication(new Date()).quantity(2).build();
        when(bookService.getAllBooks(null, null, null)).thenReturn(List.of(book, book2));

        mockMvc.perform(get("/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].isbn").value("9780312850098"))
                .andExpect(jsonPath("$[1].name").value("test2"));
    }


    @Test
    public void bookController_saveBook() throws Exception {
        Book book = Book.builder().isbn("9780312850097").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.empty());
        when(bookService.saveBook(book)).thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(book))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780312850097"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.quantity").value(1));
    }
//todo переробити, бомає бути більш правильний варіант(тобто не асерт тру, а контеінс чи щось таке)andExpect(result -> Assertions.assertTrue(result.getResolvedException().getMessage().contains("Book with isbn 9780312850098 already exist")));
    @Test
    public void bookController_saveBookThatAlreadyExist() throws Exception {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookService.getBookByIsbn(anyString())).thenReturn(Optional.of(book));
//        when(bookService.saveBook(book)).thenThrow(new BookNotSavedException("Book with isbn 9780312850098 already exist"));
//        when(bookService.saveBook(book)).thenReturn(book);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(book))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BookNotSavedException))
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException().getMessage().contains("Book with isbn 9780312850098 already exist")));
//                .andExpect(result -> Assertions.assertEquals("Book with isbn 9780312850098 already exist", result.getResolvedException().getMessage()));
    }


    @Test
    public void bookController_updateBookThatExistTest() throws Exception{
        Book book = Book.builder().isbn("9780312850097").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookService.updateBook("9780312850097",book)).thenReturn(book);

        mockMvc.perform(patch("/books/{isbn}","9780312850097")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(book))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780312850097"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.quantity").value(1));
    }

    @Test
    public void bookController_deleteBookThatExistTest() throws Exception{
        Book book = Book.builder().isbn("9780312850097").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookService.deleteBookByIsbn(anyString())).thenReturn(book);


        mockMvc.perform(delete("/books/{isbn}","9780312850097")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(book))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9780312850097"))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.quantity").value(1));
    }


}
