package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    //todo solve n+1 problem everywhere
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void updateBook(String isbn, Book book) {
        book.setIsbn(isbn);
        bookRepository.save(book);
    }


}
