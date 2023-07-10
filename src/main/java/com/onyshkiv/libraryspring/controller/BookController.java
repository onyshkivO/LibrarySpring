package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.DTO.AuthorDTO;
import com.onyshkiv.libraryspring.DTO.BookDTO;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.exception.book.BookNotCreatedException;
import com.onyshkiv.libraryspring.service.BookService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @Autowired
    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        List<BookDTO> books = bookService.getAllBooks()
                .stream()
                .map(this::convertToBookDTO)
                .toList();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBookByIsbn(@PathVariable("isbn") String isbn) {
        Book book = bookService.getBookByIsbn(isbn);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<BookDTO> saveBook(@RequestBody @Valid BookDTO book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new BookNotCreatedException(bindingResult.getFieldErrors().toString());
        Book savedBook = bookService.saveBook(convertToBook(book));
        return new ResponseEntity<>(convertToBookDTO(savedBook), HttpStatus.OK);
    }

    @PatchMapping("/{isbn}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable("isbn") String isbn, @RequestBody @Valid BookDTO book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BookNotCreatedException(bindingResult.getFieldErrors().toString());//todo може то краще робити через ті валідатори
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
