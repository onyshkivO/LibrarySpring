package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.author.AuthorNotFoundException;
import com.onyshkiv.libraryspring.exception.author.AuthorNotSavedException;
import com.onyshkiv.libraryspring.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebMvcTest(AuthorService.class)
public class AuthorServiceTest {
    @MockBean
    private AuthorRepository authorRepository;

    @InjectMocks
    @Autowired
    private AuthorService authorService;

    @Test
    public void getAuthorByIdWhenExistTest() {
        when(authorRepository.findById(anyInt()))
                .thenReturn(Optional.of(new Author(1, "Author")));

        Author author = authorService.getAuthorById(1);
        assertThat(author.getId()).isEqualTo(1);
        assertThat(author.getName()).isEqualTo("Author");
    }

    @Test
    public void getAuthorByIdWhenNotExistTest() {
        when(authorRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getAuthorById(anyInt()))
                .isInstanceOf(AuthorNotFoundException.class);
    }

    @Test
    public void getAuthorsTest() {
        Page<Author> authorPage = new PageImpl<>(List.of(
                new Author(1, "Author1"),
                new Author(2, "Author2"),
                new Author(3, "Author3")
        ));
        when(authorRepository.findAll((Pageable) any()))
                .thenReturn(authorPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<Author> authorPageResult = authorService.getAllAuthors(pageable);
        List<Author> authors = authorPageResult.getData();
        assertThat(authors.get(0).getId()).isEqualTo(1);
        assertThat(authors.get(0).getName()).isEqualTo("Author1");
        assertThat(authors.get(1).getId()).isEqualTo(2);
        assertThat(authors.get(1).getName()).isEqualTo("Author2");
        assertThat(authors.get(2).getId()).isEqualTo(3);
        assertThat(authors.get(2).getName()).isEqualTo("Author3");
    }

    @Test
    public void createAuthorWithoutIdTest() {
        Author author = new Author("Author");
        when(authorRepository.save(any())).thenReturn(author);

        author = authorService.saveAuthor(author);
        assertThat(author.getName()).isEqualTo("Author");
    }

    @Test
    public void createAuthorWithIdTest() {
        Author author = new Author(1, "Author");
        assertThatThrownBy(() -> authorService.saveAuthor(author))
                .isInstanceOf(AuthorNotSavedException.class);
        verifyNoInteractions(authorRepository);
    }

    @Test
    public void updateAuthorTest() {
        Author authorFromDb = new Author(1, "Author");
        Author author = new Author("Author(Updated)");
        when(authorRepository.findById(any())).thenReturn(Optional.of(authorFromDb));
        authorFromDb = authorService.updateAuthor(1, author);
        assertThat(authorFromDb.getId()).isEqualTo(1);
        assertThat(authorFromDb.getName()).isEqualTo("Author(Updated)");
        verify(authorRepository,times(1)).findById(anyInt());
    }

    @Test
    public void deleteAuthorTest() {
        when(authorRepository.findById(any())).thenReturn(Optional.of(new Author()));
        authorService.delete(anyInt());
        verify(authorRepository,times(1)).delete(any());
        verify(authorRepository, times(1)).findById(anyInt());
    }
}
