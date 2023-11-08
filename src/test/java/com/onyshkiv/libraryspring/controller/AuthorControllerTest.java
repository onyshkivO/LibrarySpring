package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.service.AuthorService;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthorService authorService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private MyUserDetailsService myUserDetailsService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getAuthorByIdWhenExistTest() throws Exception {
        when(authorService.getAuthorById(anyInt()))
                .thenReturn(new Author(1, "Author1"));

        mockMvc.perform(get("/authors/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Author1"));

        verify(authorService, times(1)).getAuthorById(anyInt());
    }

    @Test
    public void getAuthorsTest() throws Exception {
        List<Author> authorsList = List.of(
                new Author(1, "Author1"),
                new Author(2, "Author2"),
                new Author(3, "Author3")
        );
        when(authorService.getAllAuthors(any()))
                .thenReturn(new DataPageDto<>(authorsList, 0, 1));
        mockMvc.perform(get("/authors")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Author1"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Author2"))
                .andExpect(jsonPath("$.data[2].id").value(3))
                .andExpect(jsonPath("$.data[2].name").value("Author3"));
        verify(authorService,times(1)).getAllAuthors(any());
    }

    @Test
    public void saveAuthorWithoutErrorsTest() throws Exception {
        Author author = new Author(1, "test Author");
        String jsonAuthor = objectMapper.writeValueAsString(author);

        when(authorService.saveAuthor(any()))
                .thenReturn(author);

        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthor)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test Author"));
        verify(authorService, times(1)).saveAuthor(any());
    }

    @Test
    public void saveAuthorWithValidationErrorTest() throws Exception {
        Author author = new Author(1, "");
        String jsonPublication = objectMapper.writeValueAsString(author);
        mockMvc.perform(post("/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPublication)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is4xxClientError());
        verifyNoInteractions(authorService);
    }

    @Test
    public void updateAuthorTest() throws Exception {
        Author author = new Author(1, "test Author");
        String jsonAuthor = objectMapper.writeValueAsString(author);


        when(authorService.updateAuthor(anyInt(), any()))
                .thenReturn(author);

        mockMvc.perform(put("/authors/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonAuthor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test Author"));
        verify(authorService, times(1)).updateAuthor(anyInt(), any());
    }

    @Test
    public void deleteAuthorTest() throws Exception {
        mockMvc.perform(delete("/authors/{id}", anyInt())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authorService,times(1)).delete(anyInt());
    }
}
