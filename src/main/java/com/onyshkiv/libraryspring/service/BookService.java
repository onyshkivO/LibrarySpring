package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.repository.BookRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    public DataPageDto<Book> getAllBooks(Pageable pageable) {
        Page<Book> booksPage = bookRepository.findAll(pageable);
        return new DataPageDto<>(booksPage.getContent(), pageable.getPageNumber(), booksPage.getTotalPages());
    }

    public Book getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn)
                .orElseThrow(() -> new BookNotFoundException("There are not book with isbn" + isbn));
    }


    @Transactional
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(String isbn, Book book) {
        Book bookFromDb = getBookByIsbn(isbn);
        //todo можливо ліпше забрати тут оновлення авторів і публікацій і просто інші методи для цього зробити
        BeanUtils.copyProperties(book, bookFromDb, "isbn", "activeBooks", "publication", "authors");
//        BeanUtils.copyProperties(book, bookFromDb, "isbn", "activeBooks");
        return bookFromDb;
//        return bookRepository.save(bookFromDb); //todo перевірити чи можна це забрати
    }


    public DataPageDto<Book> findBooksByAuthor(int id, Pageable pageable) {
        Page<Book> booksPage = bookRepository.getBooksByAuthorsId(id, pageable);
        return new DataPageDto<>(booksPage.getContent(), pageable.getPageNumber(), booksPage.getTotalPages());
    }

    public DataPageDto<Book> findBooksByPublication(int id, Pageable pageable) {
        Page<Book> booksPage = bookRepository.getBooksByPublicationId(id, pageable);
        return new DataPageDto<>(booksPage.getContent(), pageable.getPageNumber(), booksPage.getTotalPages());
    }

    public DataPageDto<Book> findBooksByName(String name, Pageable pageable) {
        Page<Book> booksByNameContainingIgnoreCase = bookRepository.getBooksByNameContainingIgnoreCase(name, pageable);
        return new DataPageDto<>(booksByNameContainingIgnoreCase.getContent(),
                pageable.getPageNumber(),
                booksByNameContainingIgnoreCase.getTotalPages());
    }

    @Transactional
    public void delete(String isbn) {
        Book book = getBookByIsbn(isbn);
        book.getPublication().getBooks().remove(book);
        book.getAuthors().forEach(author -> author.getBooks().remove(book));
        bookRepository.delete(book);
    }
}
