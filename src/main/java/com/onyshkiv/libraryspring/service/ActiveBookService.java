package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.repository.ActiveBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ActiveBookService {
    private final ActiveBookRepository activeBookRepository;

    @Autowired
    public ActiveBookService(ActiveBookRepository activeBookRepository) {
        this.activeBookRepository = activeBookRepository;
    }

    public List<ActiveBook> getAllActiveBooks() {
        return activeBookRepository.findAll();
    }


    @Transactional
    public void saveActiveBook(ActiveBook activeBook) {
        activeBookRepository.save(activeBook);
    }

    @Transactional
    public void updateActiveBook(Integer id, ActiveBook activeBook) {
        activeBook.setActiveBookId(id);
        activeBookRepository.save(activeBook);
    }
}
