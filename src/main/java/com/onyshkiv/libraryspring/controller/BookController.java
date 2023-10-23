package com.onyshkiv.libraryspring.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.Views;
import com.onyshkiv.libraryspring.exception.book.BookNotSavedException;
import com.onyshkiv.libraryspring.service.BookService;
import com.onyshkiv.libraryspring.util.BookValidator;
import jakarta.validation.Valid;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    private final BookValidator bookValidator;

    @Autowired
    public BookController(BookService bookService, BookValidator bookValidator) {
        this.bookService = bookService;

        this.bookValidator = bookValidator;
    }

    @GetMapping()
    @JsonView(Views.FullBook.class)
    public ResponseEntity<DataPageDto<Book>> getAllBooks(@PageableDefault Pageable pageable) {
        DataPageDto<Book> books = bookService.getAllBooks(pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    //todo якщо я просто хотів книжки якогось автора то можна просто метод getBooks викликати відповідного автора чи публікації
    @GetMapping("/author/{id}")
    @JsonView(Views.FullBook.class)
    public ResponseEntity<DataPageDto<Book>> getBooksByAuthorId(@PathVariable("id") int id, @PageableDefault Pageable pageable) {
        DataPageDto<Book> books = bookService.findBooksByAuthor(id, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/publication/{id}")
    @JsonView(Views.IdName.class)
    public ResponseEntity<DataPageDto<Book>> getBooksByPublicationId(@PathVariable("id") int id, @PageableDefault Pageable pageable) {
        DataPageDto<Book> books = bookService.findBooksByPublication(id, pageable);
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/search")
    @JsonView(Views.FullBook.class)
    public ResponseEntity<DataPageDto<Book>> getBooksByName(@RequestParam(value = "name", required = false) String name, @PageableDefault Pageable pageable) {
        DataPageDto<Book> books = bookService.findBooksByName(name, pageable);
        System.out.println(books.getData());
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{isbn}")
    @JsonView(Views.FullBook.class)
    public ResponseEntity<Book> getBookByIsbn(@PathVariable("isbn") Book book) {
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    //todo перевірити що буде, якщо там не задати авторів або/і публікацію(так воно ламається, тому якось то пофіксити)
    @PostMapping()
    @JsonView(Views.FullBook.class)
    public ResponseEntity<Book> saveBook(@RequestBody @Valid Book book, BindingResult bindingResult) {
        System.out.println(book.getAuthors());
        bookValidator.validate(book, bindingResult);
        if (bindingResult.hasErrors()) throw new BookNotSavedException(bindingResult.getFieldErrors().toString());
        Book savedBook = bookService.saveBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.OK);
    }


    //todo розібратися з тими валідаторами, чи треба їх в update щоб проблем з id не було(типу просто оновлюєш, а воно каже таке id вже є)
    //todo зробити може щоб якось і міняти і авторів і публікації
    @PutMapping("/{isbn}")
    @JsonView(Views.FullBook.class)
    public ResponseEntity<Book> updateBook(@PathVariable("isbn") Book bookFromDb, @RequestBody @Valid Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new BookNotSavedException(bindingResult.getFieldErrors().toString());

        Book updatedBook = bookService.updateBook(bookFromDb, book);
        return new ResponseEntity<>(updatedBook, HttpStatus.OK);
    }

    @DeleteMapping("/{isbn}")
    public void deleteBookByIsbn(@PathVariable("isbn") Book book) {
        bookService.delete(book);
    }


}
