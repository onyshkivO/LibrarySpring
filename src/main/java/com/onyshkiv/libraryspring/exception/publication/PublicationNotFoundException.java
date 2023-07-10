package com.onyshkiv.libraryspring.exception.publication;

import com.onyshkiv.libraryspring.exception.MyException;

public class PublicationNotFoundException extends MyException {
    public PublicationNotFoundException(String message) {
        super(message);
    }
}
