package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.*;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotFoundException;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.exception.author.AuthorNotSavedException;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotFoundException;
import com.onyshkiv.libraryspring.repository.ActiveBookRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@WebMvcTest(ActiveBookService.class)
public class ActiveBookServiceTest {
    @MockBean
    private ActiveBookRepository activeBookRepository;
    @MockBean
    private BookService bookService;

    @InjectMocks
    @Autowired
    private ActiveBookService activeBookService;

    @Test
    public void getActiveBookByIdWhenExistTest(){
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .startDate(LocalDate.of(2021, 8, 12))
                .endDate(LocalDate.of(2021, 9, 12))
                .fine(100.00)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        when(activeBookRepository.findById(anyInt())).thenReturn(Optional.of(activeBook));

        activeBook = activeBookService.getActiveBookById(anyInt());
        assertThat(activeBook.getId()).isEqualTo(1);
        assertThat(activeBook.getStartDate()).isEqualTo(LocalDate.of(2021, 8, 12));
        assertThat(activeBook.getEndDate()).isEqualTo(LocalDate.of(2021, 9, 12));
        assertThat(activeBook.getFine()).isEqualTo(100.00);
        assertThat(activeBook.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        verify(activeBookRepository,times(1)).findById(anyInt());
    }
    @Test
    public void getActiveBookByIdWhenNotExistTest(){
        when(activeBookRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> activeBookService.getActiveBookById(anyInt()))
                .isInstanceOf(ActiveBookNotFoundException.class);
    }

    @Test
    public void getActiveBooks(){
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

        Page<ActiveBook> activeBookPage = new PageImpl<>(List.of(
                activeBook1,
                activeBook2,
                activeBook3
        ));
        when(activeBookRepository.findAll((Pageable) any())).thenReturn(activeBookPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<ActiveBook> activeBookPageResult = activeBookService.getAllActiveBooks(pageable);
        List<ActiveBook> activeBooks = activeBookPageResult.getData();

        assertThat(activeBooks.get(0).getId()).isEqualTo(1);
        assertThat(activeBooks.get(0).getStartDate()).isEqualTo(LocalDate.of(2021, 8, 12));
        assertThat(activeBooks.get(0).getEndDate()).isEqualTo(LocalDate.of(2021, 9, 12));
        assertThat(activeBooks.get(0).getFine()).isEqualTo(100.00);
        assertThat(activeBooks.get(0).getSubscriptionStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

        assertThat(activeBooks.get(1).getId()).isEqualTo(2);
        assertThat(activeBooks.get(1).getStartDate()).isEqualTo(LocalDate.of(2022, 8, 12));
        assertThat(activeBooks.get(1).getEndDate()).isEqualTo(LocalDate.of(2022, 9, 12));
        assertThat(activeBooks.get(1).getFine()).isEqualTo(200.00);
        assertThat(activeBooks.get(1).getSubscriptionStatus()).isEqualTo(SubscriptionStatus.RETURNED);

        assertThat(activeBooks.get(2).getId()).isEqualTo(3);
        assertThat(activeBooks.get(2).getStartDate()).isEqualTo(LocalDate.of(2023, 8, 12));
        assertThat(activeBooks.get(2).getEndDate()).isEqualTo(LocalDate.of(2023, 9, 12));
        assertThat(activeBooks.get(2).getFine()).isEqualTo(300.00);
        assertThat(activeBooks.get(2).getSubscriptionStatus()).isEqualTo(SubscriptionStatus.FINED);

        verify(activeBookRepository,times(1)).findAll(pageable);
    }


    @Test
    public void saveActiveBookWithNoIdTest(){
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
                .user(user)
                .book(book)
                .build();

        when(bookService.getBookByIsbn(anyString())).thenReturn(book);
        when(activeBookRepository.save(any())).thenReturn(activeBook);

        activeBook = activeBookService.saveActiveBook(activeBook);

        assertThat(activeBook.getStartDate()).isEqualTo(LocalDate.of(2023, 10, 27));
        assertThat(activeBook.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.WAITING);
        assertThat(activeBook.getUser()).isEqualTo(user);
        assertThat(activeBook.getBook()).isEqualTo(book);
        assertThat(book.getQuantity()).isEqualTo(11);
        verify(bookService,times(1)).getBookByIsbn(anyString());
        verify(activeBookRepository,times(1)).save(any());
    }
    @Test
    public void createActiveBookWithIdTest() {
        ActiveBook activeBook = ActiveBook.builder()
                .id(1)
                .build();
        assertThatThrownBy(() -> activeBookService.saveActiveBook(activeBook))
                .isInstanceOf(ActiveBookNotSavedException.class);
        verifyNoInteractions(activeBookRepository);
    }

    @Test
    public void updateActiveBookTest(){
        ActiveBook activeBookFromDb = ActiveBook.builder()
                .id(1)
                .subscriptionStatus(SubscriptionStatus.WAITING)
                .build();
        ActiveBook activeBook = ActiveBook.builder()
                .id(123)
                .subscriptionStatus(SubscriptionStatus.FINED)
                .endDate(LocalDate.of(2023, 11, 27))
                .build();

        activeBookFromDb = activeBookService.updateActiveBook(activeBookFromDb,activeBook);

        assertThat(activeBookFromDb.getId()).isEqualTo(1);
        assertThat(activeBookFromDb.getEndDate()).isEqualTo(LocalDate.of(2023, 11, 27));
        assertThat(activeBookFromDb.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(activeBookFromDb.getFine()).isEqualTo(100.00);
    }
    @Test
    public void returnActiveBookTest(){
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
                .user(user)
                .book(book)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();
        when(bookService.getBookByIsbn(anyString())).thenReturn(book);

        activeBookService.returnActiveBook(activeBook);
        assertThat(activeBook.getSubscriptionStatus()).isEqualTo(SubscriptionStatus.RETURNED);
        assertThat(activeBook.getUser()).isEqualTo(user);
        assertThat(activeBook.getBook()).isEqualTo(book);
        assertThat(book.getQuantity()).isEqualTo(13);
        verify(bookService,times(1)).getBookByIsbn(anyString());
    }

    @Test
    public void deleteActiveBookThatWasReturnedTest(){
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
                .user(user)
                .book(book)
                .subscriptionStatus(SubscriptionStatus.RETURNED)
                .build();

        assertThat(book.getQuantity()).isEqualTo(12);
        activeBookService.delete(activeBook);
        verify(activeBookRepository,times(1)).delete(any());
        verifyNoInteractions(bookService);
    }

    @Test
    public void deleteActiveBookThatWasNotReturnedTest(){
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
                .user(user)
                .book(book)
                .subscriptionStatus(SubscriptionStatus.ACTIVE)
                .build();


        activeBookService.delete(activeBook);
        assertThat(book.getQuantity()).isEqualTo(13);
        verify(activeBookRepository,times(1)).delete(any());
    }
}
