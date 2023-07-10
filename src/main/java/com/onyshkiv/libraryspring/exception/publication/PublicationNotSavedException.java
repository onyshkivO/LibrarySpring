package com.onyshkiv.libraryspring.exception.publication;

import com.onyshkiv.libraryspring.exception.MyException;

public class PublicationNotSavedException extends MyException {
    public PublicationNotSavedException(String message) {
        super(message);
    }
}
