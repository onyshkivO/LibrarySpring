package com.onyshkiv.libraryspring.exception.book;

import com.onyshkiv.libraryspring.exception.MyException;

public class BookNotSavedException extends MyException {
    public BookNotSavedException(String message) {
        super(message);
    }
}
