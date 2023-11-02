package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.SubscriptionStatus;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotFoundException;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.repository.ActiveBookRepository;
import com.onyshkiv.libraryspring.util.ActiveBookValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;


//todo як би краще зробити з тими статусами, що енам, тобто чи їх долдати в дто чи в окремому методі(наприклад updateStatus ставити статус і викликати update метод)
@Service
@Transactional(readOnly = true)
public class ActiveBookService {
    private final ActiveBookRepository activeBookRepository;
    private final BookService bookService;



    @Autowired
    public ActiveBookService(ActiveBookRepository activeBookRepository, BookService bookService) {
        this.activeBookRepository = activeBookRepository;
        this.bookService = bookService;
    }

    public DataPageDto<ActiveBook> getAllActiveBooks(Pageable pageable) {
        Page<ActiveBook> activeBooksPage = activeBookRepository.findAll(pageable);
        return new DataPageDto<>(activeBooksPage.getContent(), pageable.getPageNumber(), activeBooksPage.getTotalPages());
    }

    public ActiveBook getActiveBookById(int id) {
        return activeBookRepository.findById(id)
                .orElseThrow(() -> new ActiveBookNotFoundException("There are not active book with id " + id));
    }

    public DataPageDto<ActiveBook> getActiveBooksByUserLogin(String login, Pageable pageable) {
        Page<ActiveBook> activeBooksPage = activeBookRepository.getActiveBooksByUserLogin(login, pageable);
        return new DataPageDto<>(activeBooksPage.getContent(), pageable.getPageNumber(), activeBooksPage.getTotalPages());

    }


    @Transactional
    public ActiveBook saveActiveBook(ActiveBook activeBook) {
        if (activeBook.getId() != 0) {
            throw new ActiveBookNotSavedException("Active book with id " + activeBook.getId() + " already exist");
        }

        String isbn = activeBook.getBook().getIsbn();
        Book book = bookService.getBookByIsbn(isbn);
        book.setQuantity(book.getQuantity() - 1);

        activeBook.setSubscriptionStatus(SubscriptionStatus.WAITING);
        activeBook.setStartDate(LocalDate.now());
        return activeBookRepository.save(activeBook);
    }

    @Transactional
    public ActiveBook updateActiveBook(int id, ActiveBook activeBook) {
        ActiveBook activeBookFromDb = getActiveBookById(id);

        activeBookFromDb.setFine(activeBook.getFine() == null ? 100.00 : activeBook.getFine());
        activeBookFromDb.setEndDate(activeBook.getEndDate() == null ? activeBook.getStartDate().plusDays(20) : activeBook.getEndDate());
        activeBookFromDb.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        return activeBookFromDb;
    }

    @Transactional
    public ActiveBook returnActiveBook(int id) {
        ActiveBook activeBook = getActiveBookById(id);
        if (!activeBook.getSubscriptionStatus().equals(SubscriptionStatus.RETURNED)) {
            Book book = activeBook.getBook();
            book.setQuantity(book.getQuantity() + 1);
        }
        activeBook.setSubscriptionStatus(SubscriptionStatus.RETURNED);

        //todo попробувати це забрати
        return activeBook;
//        return activeBookRepository.save(activeBook);
    }

    @Transactional
    public void delete(int id) {
        ActiveBook activeBook =getActiveBookById(id);
        if (!activeBook.getSubscriptionStatus().equals(SubscriptionStatus.RETURNED)) {
            Book book = activeBook.getBook();
            book.setQuantity(book.getQuantity() + 1);
        }
        activeBookRepository.delete(activeBook);
    }
}
