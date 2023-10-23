package com.onyshkiv.libraryspring.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Views;
import com.onyshkiv.libraryspring.exception.author.AuthorNotSavedException;
import com.onyshkiv.libraryspring.service.AuthorService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/authors")
public class AuthorController {
    private final AuthorService authorService;


    @Autowired
    public AuthorController(AuthorService authorService, ModelMapper modelMapper) {
        this.authorService = authorService;

    }


    @PostMapping()
    @JsonView(Views.IdName.class)
    public ResponseEntity<Author> createAuthor(@RequestBody @Valid Author author,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new AuthorNotSavedException(bindingResult.getFieldErrors().toString());
        Author savedAuthor = authorService.saveAuthor(author);
        return new ResponseEntity<>(savedAuthor, HttpStatus.OK);

    }

    @PutMapping("/{id}")
    @JsonView(Views.FullAuthor.class)
    public ResponseEntity<Author> updateAuthor(@PathVariable("id") Author authorFromDb,
                                               @RequestBody() @Valid Author author,
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new AuthorNotSavedException(bindingResult.getFieldErrors() + " Bad name ");
        //от хз, може тут краще щоб сервіс не вертав нічого, адже і так authorFromDb буде оновлений тут
        Author updatedAuthor = authorService.updateAuthor(authorFromDb, author);
        return new ResponseEntity<>(updatedAuthor, HttpStatus.OK);
    }

    @GetMapping()
    @JsonView(Views.FullAuthor.class)
    public ResponseEntity<DataPageDto<Author>> getAuthors(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        DataPageDto<Author> authors = authorService.getAllAuthors(pageable);
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @JsonView(Views.FullAuthor.class)
    public ResponseEntity<Author> getAuthorById(@PathVariable("id") Author author) {
        return new ResponseEntity<>(author, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public void deleteAuthorById(@PathVariable("id") Author author) {
        authorService.delete(author);
    }


}
