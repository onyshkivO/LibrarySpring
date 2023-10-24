package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.SubscriptionStatus;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotFoundException;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.repository.ActiveBookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;


//todo як би краще зробити з тими статусами, що енам, тобто чи їх долдати в дто чи в окремому методі(наприклад updateStatus ставити статус і викликати update метод)
@Service
@Transactional(readOnly = true)
public class ActiveBookService {
    private final ActiveBookRepository activeBookRepository;
    private final UserService userService;
    private final BookService bookService;


    @Autowired
    public ActiveBookService(ActiveBookRepository activeBookRepository, UserService userService, BookService bookService) {
        this.activeBookRepository = activeBookRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public DataPageDto<ActiveBook> getAllActiveBooks(Pageable pageable) {
        Page<ActiveBook> activeBooksPage = activeBookRepository.findAll(pageable);
        return new DataPageDto<>(activeBooksPage.getContent(), pageable.getPageNumber(), activeBooksPage.getTotalPages());
    }

    public Optional<ActiveBook> getActiveBookById(int id) {
        return activeBookRepository.findById(id);
    }

    public Page<ActiveBook> getActiveBooksByUserLogin(String login, Pageable pageable) {
//        Optional<User> optionalUser = userService.getUserByLogin(login);
//        if (optionalUser.isEmpty()) throw new UserNotFoundException("There are not user with login " + login);
        return activeBookRepository.getActiveBooksByUserLogin(login, pageable);
    }


    @Transactional
    public ActiveBook saveActiveBook(ActiveBook activeBook) {
        if (activeBook.getId() != 0) {
            throw new ActiveBookNotSavedException("Active book with id " + activeBook.getId() + " already exist");
        }

        String isbn = activeBook.getBook().getIsbn();
        Optional<Book> optionalBook = bookService.getBookByIsbn(isbn);
        if (optionalBook.isEmpty())
            throw new BookNotFoundException("There are not book with isbn " + isbn);

        Book book = optionalBook.get();
        book.setQuantity(book.getQuantity() - 1);
//        bookService.updateBook(book, book);

        activeBook.setSubscriptionStatus(SubscriptionStatus.WAITING);
        activeBook.setStartDate(LocalDate.now());
//        if (activeBook.getEndDate() == null)
//            activeBook.setEndDate(LocalDate.now().plusDays(20));
//        if (activeBook.getFine()==null)
//            activeBook.setFine(100.00);
        return activeBookRepository.save(activeBook);
    }

    @Transactional
    public ActiveBook updateActiveBook(ActiveBook activeBookFromDb, ActiveBook activeBook) {
//        Optional<ActiveBook> optionalActiveBook = activeBookRepository.findById(id);
//        if (optionalActiveBook.isEmpty())
//            throw new ActiveBookNotFoundException("There are no active book with id " + id);
//        activeBook.setId(id);
//        activeBook.setSubscriptionStatus(SubscriptionStatus.WAITING);
//        activeBook.setStartDate(new Date());

        //хз просто як то обновляти activeBook
        activeBookFromDb.setFine(activeBook.getFine());
        activeBookFromDb.setEndDate(activeBook.getEndDate());
        activeBookFromDb.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        return activeBookRepository.save(activeBookFromDb);
    }

    @Transactional
    public ActiveBook returnActiveBook(ActiveBook activeBook) {
//        Optional<ActiveBook> optionalActiveBook = activeBookRepository.findById(activeBookId);
//        if (optionalActiveBook.isEmpty())
//            throw new ActiveBookNotFoundException("There are not active book with id " + activeBookId);
//        ActiveBook activeBook = optionalActiveBook.get();

        String isbn = activeBook.getBook().getIsbn();
        Optional<Book> optionalBook = bookService.getBookByIsbn(isbn);
        if (optionalBook.isEmpty())
            throw new BookNotFoundException("There are not book with isbn " + isbn);//зайве напевно

        Book book = optionalBook.get();
        book.setQuantity(book.getQuantity() + 1);
        bookService.updateBook(book, book);

        activeBook.setSubscriptionStatus(SubscriptionStatus.RETURNED);
        return activeBookRepository.save(activeBook);
//        activeBookRepository.updateSubscriptionStatus(activeBook.getId(),SubscriptionStatus.RETURNED);
////перевірити, чи можна так, адже я вже отримую через PathVariable
////        activeBook.setSubscriptionStatus(SubscriptionStatus.RETURNED);
//        return activeBook;
    }


    @Transactional
    public ActiveBook deleteActiveBookById(int id) {

        Optional<ActiveBook> optionalActiveBook = activeBookRepository.findById(id);
        if (optionalActiveBook.isEmpty())
            throw new ActiveBookNotFoundException("There are no active book with id " + id);
        ActiveBook activeBook = optionalActiveBook.get();
        String isbn = activeBook.getBook().getIsbn();
        Optional<Book> optionalBook = bookService.getBookByIsbn(isbn);
        if (optionalBook.isEmpty())
            throw new BookNotFoundException("There are not book with isbn " + isbn);

        if (!activeBook.getSubscriptionStatus().equals(SubscriptionStatus.RETURNED)) {
            Book book = optionalBook.get();
            book.setQuantity(book.getQuantity() + 1);
            bookService.updateBook(book, book);
        }
        activeBookRepository.deleteById(id);
        return activeBook;
    }

    public void delete(ActiveBook activeBook) {
        if (!activeBook.getSubscriptionStatus().equals(SubscriptionStatus.RETURNED)) {
            Book book = activeBook.getBook();
            book.setQuantity(book.getQuantity() + 1);
            bookService.updateBook(book, book);
        }
        activeBookRepository.delete(activeBook);
    }
}
