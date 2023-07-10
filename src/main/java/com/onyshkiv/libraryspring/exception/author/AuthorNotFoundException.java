package com.onyshkiv.libraryspring.exception.author;

import com.onyshkiv.libraryspring.exception.MyException;

public class AuthorNotFoundException extends MyException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
