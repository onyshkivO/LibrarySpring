package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.service.BookService;
import com.onyshkiv.libraryspring.util.BookValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private BookValidator bookValidator;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getBookByIsbnTest() throws Exception {
        Book book = Book.builder()
                .isbn("1")
                .name("Book1")
                .dateOfPublication(LocalDate.of(2023, 8, 12))
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication"))
                .authors(Set.of(new Author(1, "author")))
                .activeBooks(Set.of(new ActiveBook(1), new ActiveBook(2)))
                .build();
        when(bookService.getBookByIsbn(anyString()))
                .thenReturn(book);

        mockMvc.perform(get("/books/{isbn}", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value(1))
                .andExpect(jsonPath("$.name").value("Book1"))
                .andExpect(jsonPath("$.dateOfPublication").value(LocalDate.of(2023, 8, 12).toString()))
                .andExpect(jsonPath("$.quantity").value(12))
                .andExpect(jsonPath("$.details").value("some info"))
                .andExpect(jsonPath("$.publication.name").value("Publication"))
                .andExpect(jsonPath("$.authors[0].name").value("author"))
                .andExpect(jsonPath("$.activeBooks").doesNotExist());
        verify(bookService, times(1)).getBookByIsbn(anyString());
    }


    @Test
    public void getBooksTest() throws Exception {
        Book book1 = Book.builder()
                .isbn("1")
                .name("Book1").build();
        Book book2 = Book.builder()
                .isbn("2")
                .name("Book2").build();
        Book book3 = Book.builder()
                .isbn("3")
                .name("Book3").build();
        List<Book> booksList = List.of(
                book1,
                book2,
                book3
        );

        when(bookService.getAllBooks(any()))
                .thenReturn(new DataPageDto<>(booksList, 0, 1));

        mockMvc.perform(get("/books")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].isbn").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Book1"))
                .andExpect(jsonPath("$.data[1].isbn").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Book2"))
                .andExpect(jsonPath("$.data[2].isbn").value(3))
                .andExpect(jsonPath("$.data[2].name").value("Book3"));
        verify(bookService, times(1)).getAllBooks(any());
    }

    @Test
    public void createBookWithoutErrorsTest() throws Exception {
        Book book = Book.builder()
                .isbn("1231231231")
                .name("Book1")
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication"))
                .authors(Set.of(new Author(1, "author")))
                .activeBooks(Set.of(new ActiveBook(1), new ActiveBook(2)))
                .build();
        String bookJson = objectMapper.writeValueAsString(book);

        when(bookService.saveBook(any()))
                .thenReturn(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("1231231231"))
                .andExpect(jsonPath("$.name").value("Book1"))
//                .andExpect(jsonPath("$.dateOfPublication").value(LocalDate.of(2023, 8, 12).toString()))
                .andExpect(jsonPath("$.quantity").value(12))
                .andExpect(jsonPath("$.details").value("some info"))
                .andExpect(jsonPath("$.publication.name").value("Publication"))
                .andExpect(jsonPath("$.authors[0].name").value("author"))
                .andExpect(jsonPath("$.activeBooks").doesNotExist());
        verify(bookService, times(1)).saveBook(any());
    }


    //todo ще додати коли валідатор використовуєтсья
    @Test
    public void createBookWithErrorsTest() throws Exception {
        Book book = Book.builder()
                .isbn("1")
                .name("Book1")
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication"))
                .authors(Set.of(new Author(1, "author")))
                .activeBooks(Set.of(new ActiveBook(1), new ActiveBook(2)))
                .build();
        String bookJson = objectMapper.writeValueAsString(book);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof BookNotSavedException));
        verifyNoInteractions(bookService);
    }

    @Test
    public void updateBookTest() throws Exception {
        Book book = Book.builder()
                .isbn("1231231231")
                .name("Book1")
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication"))
                .authors(Set.of(new Author(1, "author")))
                .activeBooks(Set.of(new ActiveBook(1), new ActiveBook(2)))
                .build();
        String bookJson = objectMapper.writeValueAsString(book);


        when(bookService.updateBook(anyString(), any()))
                .thenReturn(book);

        mockMvc.perform(put("/books/{isbn}", "1231231231")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("1231231231"))
                .andExpect(jsonPath("$.name").value("Book1"))
                .andExpect(jsonPath("$.quantity").value(12))
                .andExpect(jsonPath("$.details").value("some info"))
                .andExpect(jsonPath("$.publication.name").value("Publication"))
                .andExpect(jsonPath("$.authors[0].name").value("author"))
                .andExpect(jsonPath("$.activeBooks").doesNotExist());
        verify(bookService, times(1)).updateBook(anyString(), any());
    }

    @Test
    public void deleteBookTest() throws Exception {
        mockMvc.perform(delete("/books/{isbn}", "123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookService, times(1)).delete(anyString());
    }

}
