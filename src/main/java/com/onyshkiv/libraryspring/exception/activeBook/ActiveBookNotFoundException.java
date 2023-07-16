package com.onyshkiv.libraryspring.exception.activeBook;

import com.onyshkiv.libraryspring.exception.MyException;

public class ActiveBookNotFoundException extends MyException {
    public ActiveBookNotFoundException(String message) {
        super(message);
    }
}
