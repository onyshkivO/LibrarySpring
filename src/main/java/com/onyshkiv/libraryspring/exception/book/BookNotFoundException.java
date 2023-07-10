package com.onyshkiv.libraryspring.exception.book;

import com.onyshkiv.libraryspring.exception.MyException;

public class BookNotFoundException extends MyException {
    public BookNotFoundException(String message) {
        super(message);
    }
}
