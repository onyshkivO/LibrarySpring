package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.SubscriptionStatus;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotFoundException;
import com.onyshkiv.libraryspring.repository.ActiveBookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;


//todo як би краще зробити з тими статусами, що енам, тобто чи їх долдати в дто чи в окремому методі(наприклад updateStatus ставити статус і викликати update метод)
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

    public Optional<ActiveBook> getActiveBookById(int id) {
        return activeBookRepository.findById(id);
    }


    @Transactional
    public ActiveBook saveActiveBook(ActiveBook activeBook) {
        if (activeBook.getActiveBookId() != 0) {
            throw new ActiveBookNotSavedException("Active book with id " + activeBook.getActiveBookId() + " already exist");
        }
        activeBook.setSubscriptionStatus(SubscriptionStatus.WAITING);
        activeBook.setStartDate(new Date());
        return activeBookRepository.save(activeBook);
    }

    @Transactional
    public ActiveBook updateActiveBook(Integer id, ActiveBook activeBook) {
        Optional<ActiveBook> optionalActiveBook = activeBookRepository.findById(id);
        if (optionalActiveBook.isEmpty())
            throw new ActiveBookNotFoundException("There are no active book with id " + id);
        activeBook.setActiveBookId(id);
        activeBook.setSubscriptionStatus(SubscriptionStatus.WAITING);
        activeBook.setStartDate(new Date());
        return activeBookRepository.save(activeBook);
    }

    @Transactional
    public ActiveBook deleteActiveBookById(int id) {
        Optional<ActiveBook> optionalActiveBook = activeBookRepository.findById(id);
        if (optionalActiveBook.isEmpty())
            throw new ActiveBookNotFoundException("There are no active book with id " + id);
        activeBookRepository.deleteById(id);
        return optionalActiveBook.get();
    }
}
