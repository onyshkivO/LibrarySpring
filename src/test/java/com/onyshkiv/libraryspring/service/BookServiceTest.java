package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@WebMvcTest(BookService.class)
public class BookServiceTest {
    @MockBean
    private BookRepository bookRepository;

    @InjectMocks
    @Autowired
    private BookService bookService;

    @Test
    public void getBookByIsbnWhenExistTest() {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .dateOfPublication(LocalDate.of(2023, 8, 12))
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();

        when(bookRepository.findById(anyString())).thenReturn(Optional.of(book));

        book = bookService.getBookByIsbn(anyString());
        assertThat(book.getIsbn()).isEqualTo("123");
        assertThat(book.getName()).isEqualTo("Book");
        assertThat(book.getDateOfPublication()).isEqualTo(LocalDate.of(2023, 8, 12));
        assertThat(book.getQuantity()).isEqualTo(12);
        assertThat(book.getDetails()).isEqualTo("some info");
        assertThat(book.getPublication()).isEqualTo(new Publication(1, "Publication"));
        verify(bookRepository).findById(anyString());
    }

    @Test
    public void getBookByIsbnWhenNotExistTest() {
        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookService.getBookByIsbn(anyString())).isInstanceOf(BookNotFoundException.class);
    }

    @Test
    public void getBooksTest() {
        Book book1 = Book.builder()
                .isbn("1")
                .name("Book1").build();
        Book book2 = Book.builder()
                .isbn("2")
                .name("Book2").build();
        Book book3 = Book.builder()
                .isbn("3")
                .name("Book3").build();

        Page<Book> bookPage = new PageImpl<>(List.of(
                book1,
                book2,
                book3
        ));

        when(bookRepository.findAll((Pageable) any()))
                .thenReturn(bookPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<Book> bookPageResult = bookService.getAllBooks(pageable);
        List<Book> books = bookPageResult.getData();
        assertThat(books.get(0).getIsbn()).isEqualTo("1");
        assertThat(books.get(0).getName()).isEqualTo("Book1");
        assertThat(books.get(1).getIsbn()).isEqualTo("2");
        assertThat(books.get(1).getName()).isEqualTo("Book2");
        assertThat(books.get(2).getIsbn()).isEqualTo("3");
        assertThat(books.get(2).getName()).isEqualTo("Book3");
        verify(bookRepository).findAll(pageable);
    }


    @Test
    public void createBookTest() {
        Book book = Book.builder()
                .isbn("123")
                .name("Book")
                .dateOfPublication(LocalDate.of(2023, 8, 12))
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication")).build();
        when(bookRepository.save(any())).thenReturn(book);

        book = bookService.saveBook(book);
        assertThat(book.getIsbn()).isEqualTo("123");
        assertThat(book.getName()).isEqualTo("Book");
        assertThat(book.getDateOfPublication()).isEqualTo(LocalDate.of(2023, 8, 12));
        assertThat(book.getQuantity()).isEqualTo(12);
        assertThat(book.getDetails()).isEqualTo("some info");
        assertThat(book.getPublication()).isEqualTo(new Publication(1, "Publication"));
        verify(bookRepository).save(any());
    }

    @Test
    public void updateBookTest() {
        Book bookFromDb = Book.builder()
                .isbn("1")
                .name("Book1")
                .dateOfPublication(LocalDate.of(2023, 8, 12))
                .quantity(12)
                .details("some info")
                .publication(new Publication(1, "Publication"))
                .authors(Set.of(new Author(1, "author")))
                .activeBooks(Set.of(new ActiveBook(1), new ActiveBook(2)))
                .build();

        Book book = Book.builder()
                .isbn("anything")
                .name("Book2")
                .dateOfPublication(LocalDate.of(2021, 8, 12))
                .quantity(1)
                .details("some info(new)")
                .publication(new Publication(2, "Publication(new)"))
                .authors(Set.of(new Author(2, "author(new)")))
                .activeBooks(new HashSet<>()).build();

        when(bookRepository.findById(anyString())).thenReturn(Optional.of(bookFromDb));
        bookFromDb = bookService.updateBook(anyString(), book);
        assertThat(bookFromDb.getIsbn()).isEqualTo("1");
        assertThat(bookFromDb.getName()).isEqualTo("Book2");
        assertThat(bookFromDb.getDateOfPublication()).isEqualTo(LocalDate.of(2021, 8, 12));
        assertThat(bookFromDb.getQuantity()).isEqualTo(1);
        assertThat(bookFromDb.getDetails()).isEqualTo("some info(new)");
//        assertThat(bookFromDb.getAuthors()).isEqualTo(Set.of(new Author(2, "author(new)")));
//        assertThat(bookFromDb.getPublication()).isEqualTo(new Publication(2, "Publication(new)"));
        assertThat(bookFromDb.getActiveBooks()).isEqualTo(Set.of(new ActiveBook(1), new ActiveBook(2)));
//        verify(bookRepository).save(bookFromDb);
        verify(bookRepository,times(1)).findById(anyString());
    }

    @Test
    public void deleteBookTest() {
        Book book = new Book("1", "Book", LocalDate.of(2021, 8, 12),
                1, "some info", new Publication(2, "Publication(new)"));
        when(bookRepository.findById(any())).thenReturn(Optional.of(book));
        bookService.delete(anyString());
        verify(bookRepository, times(1)).delete(any());
        verify(bookRepository, times(1)).findById(anyString());
    }

    @Test
    public void getBooksByAuthorIdTest() {
        Book book1 = Book.builder()
                .isbn("1")
                .name("Book1").build();
        Book book2 = Book.builder()
                .isbn("2")
                .name("Book2").build();
        Book book3 = Book.builder()
                .isbn("3")
                .name("Book3").build();

        Page<Book> bookPage = new PageImpl<>(List.of(
                book1,
                book2,
                book3
        ));

        when(bookRepository.getBooksByAuthorsId(anyInt(), any()))
                .thenReturn(bookPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<Book> bookPageResult = bookService.findBooksByAuthor(1, pageable);
        List<Book> books = bookPageResult.getData();
        assertThat(books.get(0).getIsbn()).isEqualTo("1");
        assertThat(books.get(0).getName()).isEqualTo("Book1");
        assertThat(books.get(1).getIsbn()).isEqualTo("2");
        assertThat(books.get(1).getName()).isEqualTo("Book2");
        assertThat(books.get(2).getIsbn()).isEqualTo("3");
        assertThat(books.get(2).getName()).isEqualTo("Book3");
        verify(bookRepository).getBooksByAuthorsId(1, pageable);
    }

    @Test
    public void getBooksByPublicationIdTest() {
        Book book1 = Book.builder()
                .isbn("1")
                .name("Book1").build();
        Book book2 = Book.builder()
                .isbn("2")
                .name("Book2").build();
        Book book3 = Book.builder()
                .isbn("3")
                .name("Book3").build();

        Page<Book> bookPage = new PageImpl<>(List.of(
                book1,
                book2,
                book3
        ));

        when(bookRepository.getBooksByPublicationId(anyInt(), any()))
                .thenReturn(bookPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<Book> bookPageResult = bookService.findBooksByPublication(1, pageable);
        List<Book> books = bookPageResult.getData();
        assertThat(books.get(0).getIsbn()).isEqualTo("1");
        assertThat(books.get(0).getName()).isEqualTo("Book1");
        assertThat(books.get(1).getIsbn()).isEqualTo("2");
        assertThat(books.get(1).getName()).isEqualTo("Book2");
        assertThat(books.get(2).getIsbn()).isEqualTo("3");
        assertThat(books.get(2).getName()).isEqualTo("Book3");
        verify(bookRepository).getBooksByPublicationId(1, pageable);
    }

    @Test
    public void getBooksByNameTest() {
        Book book1 = Book.builder()
                .isbn("1")
                .name("Book1").build();
        Book book2 = Book.builder()
                .isbn("2")
                .name("Book2").build();
        Book book3 = Book.builder()
                .isbn("3")
                .name("Book3").build();

        Page<Book> bookPage = new PageImpl<>(List.of(
                book1,
                book2,
                book3
        ));

        when(bookRepository.getBooksByNameContainingIgnoreCase(anyString(), any()))
                .thenReturn(bookPage);


        PageRequest pageable = PageRequest.of(0, 10);
        DataPageDto<Book> bookPageResult = bookService.findBooksByName("some", pageable);
        List<Book> books = bookPageResult.getData();
        assertThat(books.get(0).getIsbn()).isEqualTo("1");
        assertThat(books.get(0).getName()).isEqualTo("Book1");
        assertThat(books.get(1).getIsbn()).isEqualTo("2");
        assertThat(books.get(1).getName()).isEqualTo("Book2");
        assertThat(books.get(2).getIsbn()).isEqualTo("3");
        assertThat(books.get(2).getName()).isEqualTo("Book3");
        verify(bookRepository).getBooksByNameContainingIgnoreCase("some", pageable);
    }


}
