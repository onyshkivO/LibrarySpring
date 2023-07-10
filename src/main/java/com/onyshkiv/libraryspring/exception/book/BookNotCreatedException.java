package com.onyshkiv.libraryspring.exception.book;

import com.onyshkiv.libraryspring.exception.MyException;

public class BookNotCreatedException extends MyException {
    public BookNotCreatedException(String message) {
        super(message);
    }
}
