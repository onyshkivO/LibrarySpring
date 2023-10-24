package com.onyshkiv.libraryspring.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.entity.Views;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.service.ActiveBookService;
import com.onyshkiv.libraryspring.util.ActiveBookValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/activeBooks")
public class ActiveBookController {
    private final ActiveBookService activeBookService;
    private final ActiveBookValidator activeBookValidator;

    @Autowired
    public ActiveBookController(ActiveBookService activeBookService, ActiveBookValidator activeBookValidator) {
        this.activeBookService = activeBookService;
        this.activeBookValidator = activeBookValidator;
    }

    @GetMapping()
    @JsonView(Views.FullActiveBook.class)
    public ResponseEntity<DataPageDto<ActiveBook>> getAllActiveBooks(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        DataPageDto<ActiveBook> activeBooks = activeBookService.getAllActiveBooks(pageable);
        return new ResponseEntity<>(activeBooks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @JsonView(Views.FullActiveBook.class)
    public ResponseEntity<ActiveBook> getActiveBookById(@PathVariable("id") ActiveBook activeBook) {
        return new ResponseEntity<>(activeBook, HttpStatus.OK);
    }

    @GetMapping("/user/{login}")
    @JsonView(Views.FullActiveBook.class)
    public ResponseEntity<Set<ActiveBook>> getActiveBooksByUserLogin(
            @PathVariable("login") User user,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
//        Page<ActiveBook> activeBooks = activeBookService.getActiveBooksByUserLogin(login, pageable);
        return new ResponseEntity<>(user.getActiveBooks(), HttpStatus.OK);
    }

    @PostMapping()
    @JsonView(Views.FullActiveBook.class)
    public ResponseEntity<ActiveBook> saveActiveBook(@RequestBody @Valid ActiveBook activeBook, BindingResult bindingResult) {
        activeBookValidator.validate(activeBook, bindingResult);
        if (bindingResult.hasErrors())
            throw new ActiveBookNotSavedException(bindingResult.getFieldErrors().toString());

        ActiveBook savedActiveBook = activeBookService.saveActiveBook(activeBook);
        return new ResponseEntity<>(savedActiveBook, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @JsonView(Views.FullActiveBook.class)
    public ResponseEntity<ActiveBook> updateActiveBook(@PathVariable("id") ActiveBook activeBookFromDb,
                                                       @RequestBody @Valid ActiveBook activeBook,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ActiveBookNotSavedException(bindingResult.getFieldErrors().toString());

        ActiveBook updatedActiveBook = activeBookService.updateActiveBook(activeBookFromDb, activeBook);
        return new ResponseEntity<>(updatedActiveBook, HttpStatus.OK);
    }

    @PutMapping("/return/{id}")
    @JsonView(Views.FullActiveBook.class)
    public ResponseEntity<ActiveBook> returnActiveBook(@PathVariable("id") ActiveBook activeBook) {
        ActiveBook updatedActiveBook = activeBookService.returnActiveBook(activeBook);
        return new ResponseEntity<>(updatedActiveBook, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @JsonView(Views.IdName.class)
    public void deleteActiveBookById(@PathVariable("id") ActiveBook activeBook) {
        activeBookService.delete(activeBook);
    }


}
