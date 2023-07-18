package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.author.AuthorNotFoundException;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotFoundException;
import com.onyshkiv.libraryspring.repository.AuthorRepository;
import com.onyshkiv.libraryspring.repository.BookRepository;
import com.onyshkiv.libraryspring.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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


    //todo solve n+1 problem everywhere і якраз як при йьому зробити пагінацію і сортування
    public List<Book> getAllBooks(Integer page, Integer bookPerPage, String sortOption) {
        if (page != null && sortOption != null) {
            return bookRepository.findAll(PageRequest.of(page, bookPerPage, Sort.by(sortOption))).getContent();
        } else if (page != null) {
            return bookRepository.findAll(PageRequest.of(page, bookPerPage)).getContent();
        } else if (sortOption != null) {
            return bookRepository.findAll(Sort.by(sortOption));
        }
        return bookRepository.findAll();
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findById(isbn);
    }


    @Transactional
    public Book saveBook(Book book) {
        Optional<Book> optionalBook = bookRepository.findById(book.getIsbn());
        if (optionalBook.isPresent() || book.getIsbn().isBlank())
            throw new BookNotSavedException("Book with isbn " + book.getIsbn() + " already exist");
        return bookRepository.save(book);
    }

    @Transactional
    public Book updateBook(String isbn, Book book) {
        Optional<Book> optionalBook = bookRepository.findById(isbn);
        if (optionalBook.isEmpty())
            throw new BookNotFoundException("Not Book found with isbn " + isbn);
        book.setIsbn(isbn);
        return bookRepository.save(book);
    }

    @Transactional
    public Book deleteBookByIsbn(String isbn) {
        Optional<Book> optionalBook = bookRepository.findById(isbn);
        if (optionalBook.isEmpty())
            throw new BookNotFoundException("Not Book found with isbn " + isbn);
        bookRepository.deleteById(isbn);
        return optionalBook.get();
    }


    public List<Book> findBooksByAuthor(int id) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isEmpty()) throw new AuthorNotFoundException("There are not author with id " + id);
        return bookRepository.getBooksByAuthorsAuthorId(id);
    }

    public List<Book> findBooksByPublication(int id) {
        Optional<Publication> optionalPublication = publicationRepository.findById(id);
        if (optionalPublication.isEmpty())
            throw new PublicationNotFoundException("There are not publication with id " + id);
        return bookRepository.getBooksByPublicationPublicationId(id);
    }

    public List<Book> findBooksByName(String name) {
        return bookRepository.getBooksByNameStartingWith(name);
    }


}
