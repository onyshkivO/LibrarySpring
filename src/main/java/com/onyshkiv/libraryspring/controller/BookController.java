package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.DTO.BookDTO;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.exception.book.BookNotFoundException;
import com.onyshkiv.libraryspring.service.BookService;
import com.onyshkiv.libraryspring.util.BookValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final BookValidator bookValidator;

    @Autowired
    public BookController(BookService bookService, ModelMapper modelMapper, BookValidator bookValidator) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
        this.bookValidator = bookValidator;
    }

    @GetMapping()
    public ResponseEntity<List<BookDTO>> getAllBooks(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "books_per_page", required = false) Integer bookPerPage,
                                                     @RequestParam(value = "sort_option", required = false) String sortOption) {
        List<BookDTO> books = bookService.getAllBooks(page, bookPerPage, sortOption)
                .stream()
                .map(this::convertToBookDTO)
                .toList();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/author/{id}")
    public ResponseEntity<List<BookDTO>> getBooksByAuthorId(@PathVariable("id") int id) {
        List<BookDTO> books = bookService.findBooksByAuthor(id)
                .stream()
                .map(this::convertToBookDTO)
                .toList();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/publication/{id}")
    public ResponseEntity<List<BookDTO>> getBooksByPublicationId(@PathVariable("id") int id) {
        List<BookDTO> books = bookService.findBooksByPublication(id)
                .stream()
                .map(this::convertToBookDTO)
                .toList();
        ;
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> getBooksByName(@RequestParam(value = "name", required = false) String name) {
        List<BookDTO> books = bookService.findBooksByName(name)
                .stream()
                .map(this::convertToBookDTO)
                .toList();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<BookDTO> getBookByIsbn(@PathVariable("isbn") String isbn) {
        Optional<Book> optionalBook = bookService.getBookByIsbn(isbn);
        if (optionalBook.isEmpty()) throw new BookNotFoundException("Not book with isbn " + isbn);
        return new ResponseEntity<>(convertToBookDTO(optionalBook.get()), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BookDTO> saveBook(@RequestBody @Valid BookDTO book, BindingResult bindingResult) {
        bookValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors()) throw new BookNotSavedException(bindingResult.getFieldErrors().toString());
        Book savedBook = bookService.saveBook(convertToBook(book));
        return new ResponseEntity<>(convertToBookDTO(savedBook), HttpStatus.OK);
    }

    @PatchMapping("/{isbn}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("isbn") String isbn, @RequestBody @Valid BookDTO book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BookNotSavedException(bindingResult.getFieldErrors().toString());
        Book updatedBook = bookService.updateBook(isbn, convertToBook(book));
        return new ResponseEntity<>(convertToBookDTO(updatedBook), HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<BookDTO> deleteBookByIsbn(@PathVariable("isbn") String isbn) {
        Book book = bookService.deleteBookByIsbn(isbn);
        return new ResponseEntity<>(convertToBookDTO(book), HttpStatus.OK);
    }


    private BookDTO convertToBookDTO(Book book) {
        return modelMapper.map(book, BookDTO.class);
    }

    private Book convertToBook(BookDTO bookDTO) {
        return modelMapper.map(bookDTO, Book.class);
    }

}
