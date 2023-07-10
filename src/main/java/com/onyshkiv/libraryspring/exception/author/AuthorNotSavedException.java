package com.onyshkiv.libraryspring.exception.author;

import com.onyshkiv.libraryspring.exception.MyException;

public class AuthorNotSavedException extends MyException {
    public AuthorNotSavedException(String message) {
        super(message);
    }
}
