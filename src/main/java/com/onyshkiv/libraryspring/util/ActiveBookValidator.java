package com.onyshkiv.libraryspring.util;

import com.onyshkiv.libraryspring.DTO.ActiveBookDTO;
import com.onyshkiv.libraryspring.DTO.BookDTO;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.service.ActiveBookService;
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
        return ActiveBookDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ActiveBookDTO activeBook = (ActiveBookDTO) target;
        String bookIsbn = activeBook.getBook().getIsbn();
        Optional<Book> optionalBook = bookService.getBookByIsbn(bookIsbn);
        if (optionalBook.isEmpty() || optionalBook.get().getQuantity() < 1)
            errors.rejectValue("isbnOrQuantity", "", "There are not available book with isbn " + bookIsbn);

    }
}
