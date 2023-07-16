package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.DTO.ActiveBookDTO;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotSavedException;
import com.onyshkiv.libraryspring.exception.activeBook.ActiveBookNotFoundException;
import com.onyshkiv.libraryspring.service.ActiveBookService;
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
@RequestMapping("/activeBooks")
public class ActiveBookController {
    private final ActiveBookService activeBookService;
    private final ModelMapper modelMapper;

    @Autowired
    public ActiveBookController(ActiveBookService activeBookService, ModelMapper modelMapper) {
        this.activeBookService = activeBookService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public ResponseEntity<List<ActiveBookDTO>> getAllActiveBooks() {
        List<ActiveBookDTO> activeBooks = activeBookService.getAllActiveBooks()
                .stream()
                .map(this::convertToActiveBookDTO)
                .toList();
        return new ResponseEntity<>(activeBooks, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActiveBookDTO> getActiveBookById(@PathVariable("id") int id) {
        Optional<ActiveBook> optionalActiveBook = activeBookService.getActiveBookById(id);
        if (optionalActiveBook.isEmpty())
            throw new ActiveBookNotFoundException("There are no active book with id " + id);
        return new ResponseEntity<>(convertToActiveBookDTO(optionalActiveBook.get()), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<ActiveBookDTO> saveActiveBook(@RequestBody @Valid ActiveBookDTO activeBookDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ActiveBookNotSavedException(bindingResult.getFieldErrors().toString());
        ActiveBook activeBook = activeBookService.saveActiveBook(convertToActiveBook(activeBookDTO));
        return new ResponseEntity<>(convertToActiveBookDTO(activeBook), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ActiveBookDTO> updateActiveBook(@PathVariable("id") int id
            , @RequestBody @Valid ActiveBookDTO activeBookDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new ActiveBookNotSavedException(bindingResult.getFieldErrors().toString());
        ActiveBook activeBook = activeBookService.updateActiveBook(id, convertToActiveBook(activeBookDTO));
        return new ResponseEntity<>(convertToActiveBookDTO(activeBook), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ActiveBookDTO> deleteActiveBookById(@PathVariable("id") int id) {
        ActiveBook activeBook = activeBookService.deleteActiveBookById(id);
        return new ResponseEntity<>(convertToActiveBookDTO(activeBook), HttpStatus.OK);

    }


    private ActiveBookDTO convertToActiveBookDTO(ActiveBook activeBook) {
        return modelMapper.map(activeBook, ActiveBookDTO.class);
    }

    private ActiveBook convertToActiveBook(ActiveBookDTO activeBookDTO) {
        return modelMapper.map(activeBookDTO, ActiveBook.class);
    }


}
