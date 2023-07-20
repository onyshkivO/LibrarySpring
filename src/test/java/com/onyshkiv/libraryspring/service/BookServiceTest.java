package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @InjectMocks
    private BookService bookService;

    @Test
    public void bookService_getBookByIsbn_successReturnTest() {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.ofNullable(book));

        Optional<Book> returnedBook = bookService.getBookByIsbn("9780312850098");
        Assertions.assertNotNull(returnedBook);
        Assertions.assertEquals(book, returnedBook.get());
    }

    @Test
    public void BookService_getAllBooksTest() {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        Book book2 = Book.builder().isbn("9780312850045").name("test12").dateOfPublication(new Date()).quantity(2).build();
        when(bookRepository.findAll()).thenReturn(List.of(book, book2));
        List<Book> books = bookService.getAllBooks(null, null, null);
        Assertions.assertNotNull(books);
        Assertions.assertEquals(book, books.get(0));
        Assertions.assertEquals(book2, books.get(1));
    }


    @Test
    public void bookService_saveBookThatNotExistTest() {
        Book book = Book.builder().isbn("9780312850097").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());
        when(bookRepository.save(book)).thenReturn(book);

        Book savedBook = bookService.saveBook(book);
        Assertions.assertNotNull(savedBook);
        Assertions.assertEquals(book, savedBook);
    }

    @Test
    public void bookService_saveBookThatExistTest() {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.ofNullable(book));

        BookNotSavedException thrown = Assertions.assertThrows(BookNotSavedException.class, () -> {
            bookService.saveBook(book);
        });

        Assertions.assertEquals("Book with isbn " + book.getIsbn() + " already exist", thrown.getMessage());
    }


    @Test
    public void bookService_updateBookThatExistTest() {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.ofNullable(book));
        when(bookRepository.save(book)).thenReturn(book);
        Book updatedBook = bookService.updateBook(book.getIsbn(), book);
        Assertions.assertNotNull(updatedBook);
        Assertions.assertEquals(book, updatedBook);
    }

    @Test
    public void bookService_updateBookThatNotExistTest() {
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());
        BookNotFoundException thrown = Assertions.assertThrows(BookNotFoundException.class, () -> {
            bookService.updateBook(book.getIsbn(), book);
        });

        Assertions.assertEquals("Not Book found with isbn " + book.getIsbn(), thrown.getMessage());
    }


    @Test
    public void bookService_deleteBookThatExistTest(){
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.ofNullable(book));
        Book deletedBook = bookService.deleteBookByIsbn(anyString());
        verify(bookRepository).deleteById(anyString());
        Assertions.assertNotNull(deletedBook);
        Assertions.assertEquals(book,deletedBook);
    }

    @Test
    public void bookService_deleteBookThatNotExistTest(){
        Book book = Book.builder().isbn("9780312850098").name("test").dateOfPublication(new Date()).quantity(1).build();
        when(bookRepository.findById(anyString())).thenReturn(Optional.empty());
        BookNotFoundException thrown = Assertions.assertThrows(BookNotFoundException.class, () -> {
            bookService.deleteBookByIsbn(book.getIsbn());
        });

        Assertions.assertEquals("Not Book found with isbn " + book.getIsbn(), thrown.getMessage());
    }








}

