package com.onyshkiv.libraryspring.exception.user;

import com.onyshkiv.libraryspring.exception.MyException;

public class UserNotFoundException extends MyException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
