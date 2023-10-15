package com.onyshkiv.libraryspring.util;

import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class BookValidator implements Validator {
    private final BookService bookService;

    @Autowired
    public BookValidator(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Book.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Book book = (Book) target;
        if (bookService.getBookByIsbn(book.getIsbn()).isPresent())
            errors.rejectValue("isbn", "", "Book with isbn " + book.getIsbn() + " already exist");

    }
}
