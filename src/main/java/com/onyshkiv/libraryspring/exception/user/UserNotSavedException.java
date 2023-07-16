package com.onyshkiv.libraryspring.exception.user;

import com.onyshkiv.libraryspring.exception.MyException;

public class UserNotSavedException extends MyException {
    public UserNotSavedException(String message) {
        super(message);
    }
}
