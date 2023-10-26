package com.onyshkiv.libraryspring.util;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Component
public class ActiveBookValidator implements Validator {
    private final BookService bookService;

    @Autowired

    public ActiveBookValidator(BookService bookService) {
        this.bookService = bookService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return ActiveBook.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ActiveBook activeBook = (ActiveBook) target;
        String bookIsbn = activeBook.getBook().getIsbn();
        try {
            Book book = bookService.getBookByIsbn(bookIsbn);
            if (book.getQuantity() < 1)
                errors.rejectValue("isbnOrQuantity", "", "There are not available book with isbn " + bookIsbn);
        } catch (BookNotFoundException ignored) {
        }
    }
}
