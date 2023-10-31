package com.onyshkiv.libraryspring.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.entity.Views;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotSavedException;
import com.onyshkiv.libraryspring.service.PublicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/publications")
public class PublicationController {
    private final PublicationService publicationService;

    @Autowired
    public PublicationController(PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping()
    @JsonView(Views.FullPublication.class)
    public ResponseEntity<DataPageDto<Publication>> getAllPublications(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable) {
        DataPageDto<Publication> publications = publicationService.getAllPublications(pageable);
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @JsonView(Views.FullPublication.class)
    public ResponseEntity<Publication> getPublicationById(@PathVariable("id") Publication publication) {
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @PostMapping()
    @JsonView(Views.IdName.class)
    public ResponseEntity<Publication> createPublication(@RequestBody @Valid Publication publication,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new PublicationNotSavedException(bindingResult.getFieldErrors() + " bad name ");//todo як правильно тотримувати текст з binding result
        Publication savedPublication = publicationService.savePublication(publication);

        return new ResponseEntity<>(savedPublication, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @JsonView(Views.FullPublication.class)
    public ResponseEntity<Publication> updatePublication(@PathVariable("id") Publication publicationFromDb,
                                                         @RequestBody @Valid Publication publication,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new PublicationNotSavedException(bindingResult.getFieldErrors() + " bad name ");

        Publication savedPublication = publicationService.updatePublication(publicationFromDb, publication);

        return new ResponseEntity<>(savedPublication, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deletePublication(@PathVariable("id") Publication publication) {
        publicationService.delete(publication);
    }


}
