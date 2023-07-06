package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.repository.ActiveBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActiveBookService {
    private final ActiveBookRepository activeBookRepository;

    @Autowired
    public ActiveBookService(ActiveBookRepository activeBookRepository) {
        this.activeBookRepository = activeBookRepository;
    }
}
