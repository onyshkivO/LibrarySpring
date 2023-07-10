package com.onyshkiv.libraryspring.controller;


import com.onyshkiv.libraryspring.DTO.AuthorDTO;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.exception.author.AuthorNotSavedException;
import com.onyshkiv.libraryspring.service.AuthorService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorService authorService;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthorController(AuthorService authorService, ModelMapper modelMapper) {
        this.authorService = authorService;
        this.modelMapper = modelMapper;
    }


    @PostMapping()
    public ResponseEntity<AuthorDTO> createAuthor(@RequestBody @Valid AuthorDTO authorDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new AuthorNotSavedException(bindingResult.getFieldErrors().toString());
        Author author = authorService.saveAuthor(convertToAuthor(authorDTO));
        return new ResponseEntity<>(convertToAuthorDTO(author), HttpStatus.OK);

    }

    @PatchMapping("/{id}")
    public ResponseEntity<AuthorDTO> updateAuthor(@PathVariable("id") int id, @RequestBody() @Valid AuthorDTO authorDTO,
                                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new AuthorNotSavedException(bindingResult.getFieldErrors() + " Bad name ");
        Author author = authorService.updateAuthor(id, convertToAuthor(authorDTO));
        return new ResponseEntity<>(convertToAuthorDTO(author), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<AuthorDTO>> getAllActiveBooks() {
        List<AuthorDTO> authors = authorService.getAllAuthors()
                .stream()
                .map(this::convertToAuthorDTO)
                .toList();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> getAuthorById(@PathVariable("id") int id) {
        AuthorDTO author = convertToAuthorDTO(authorService.getAuthorById(id));
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthorDTO> deleteAuthorById(@PathVariable("id") int id) {
        AuthorDTO author = convertToAuthorDTO(authorService.deleteAuthorById(id));
        return new ResponseEntity<>(author, HttpStatus.OK);
    }


    private AuthorDTO convertToAuthorDTO(Author activeBook) {
        return modelMapper.map(activeBook, AuthorDTO.class);
    }

    private Author convertToAuthor(AuthorDTO authorDTO) {
        return modelMapper.map(authorDTO, Author.class);
    }
}
