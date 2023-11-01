package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.service.BookService;
import com.onyshkiv.libraryspring.util.BookValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(jsonPath("$.publication.name").value( "Publication"))
                .andExpect(jsonPath("$.authors[0].name").value("author"))
                .andExpect(jsonPath("$.activeBooks").doesNotExist());
        verify(bookService, times(1)).getBookByIsbn(anyString());
    }


}
