package com.onyshkiv.libraryspring.exception.activeBook;

import com.onyshkiv.libraryspring.exception.MyException;

public class ActiveBookNotSavedException extends MyException {
    public ActiveBookNotSavedException(String message) {
        super(message);
    }
}
