package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.repository.AuthorRepository;
import com.onyshkiv.libraryspring.repository.BookRepository;
import com.onyshkiv.libraryspring.repository.PublicationRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final PublicationRepository publicationRepository;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository, PublicationRepository publicationRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.publicationRepository = publicationRepository;
    }


    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn);
    }


    @Transactional
    public Book saveBook(Book book) {
        //є валідатор тому напевно зайве
//        Optional<Book> optionalBook = bookRepository.findById(book.getIsbn());
//        if (optionalBook.isPresent() || book.getIsbn().isBlank())
//            throw new BookNotSavedException("Book with isbn " + book.getIsbn() + " already exist");

        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(Book bookFromDb, Book book) {
//        Optional<Book> optionalBook = bookRepository.findById(isbn);
//        if (optionalBook.isEmpty())
//            throw new BookNotFoundException("Not Book found with isbn " + isbn);
//        book.setIsbn(isbn);
        //todo перевірити як працює при оновленні авторів і публікацій, чи видаляє старих авторів
        BeanUtils.copyProperties(book, bookFromDb, "isbn","activeBooks","publication","authors");
//        BeanUtils.copyProperties(book, bookFromDb, "isbn","activeBooks");
        return bookRepository.save(bookFromDb);
    }

    @Transactional
    public Book deleteBookByIsbn(String isbn) {
        Optional<Book> optionalBook = bookRepository.findById(isbn);
        if (optionalBook.isEmpty())
            throw new BookNotFoundException("Not Book found with isbn " + isbn);
        bookRepository.deleteById(isbn);
        return optionalBook.get();
    }


    public Page<Book> findBooksByAuthor(int id,Pageable pageable) {
        //думаю не треба, бо як не буде такого автора то просто пустий список книжок
//        Optional<Author> optionalAuthor = authorRepository.findById(id);
//        if (optionalAuthor.isEmpty()) throw new AuthorNotFoundException("There are not author with id " + id);

        return bookRepository.getBooksByAuthorsId(id,pageable);
    }

    public Page<Book> findBooksByPublication(int id,Pageable pageable) {
//        Optional<Publication> optionalPublication = publicationRepository.findById(id);
//        if (optionalPublication.isEmpty())
//            throw new PublicationNotFoundException("There are not publication with id " + id);

        return bookRepository.getBooksByPublicationId(id,pageable);
    }

    public Page<Book> findBooksByName(String name, Pageable pageable) {
        return bookRepository.getBooksByNameStartingWith(name,pageable);
    }


    public void delete(Book book) {
        bookRepository.delete(book);
    }
}
