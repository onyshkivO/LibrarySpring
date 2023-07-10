package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.DTO.AuthorDTO;
import com.onyshkiv.libraryspring.DTO.PublicationDTO;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.PublicationNotSavedException;
import com.onyshkiv.libraryspring.service.PublicationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/publications")
public class PublicationController {
    private final PublicationService publicationService;
    private final ModelMapper modelMapper;

    @Autowired
    public PublicationController(PublicationService publicationService, ModelMapper modelMapper) {
        this.publicationService = publicationService;
        this.modelMapper = modelMapper;
    }

    @GetMapping()
    public List<PublicationDTO> getAllPublications() {
        return publicationService.getAllPublications()
                .stream()
                .map(this::convertToPublicationDTO)
                .collect(Collectors.toList());
    }


    @PostMapping()
    public ResponseEntity<HttpStatus> createPublication(@RequestBody @Valid PublicationDTO publicationDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new PublicationNotSavedException(bindingResult.getFieldErrors() + " bad name ");

        publicationService.savePublication(convertToPublication(publicationDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> updatePublication(@PathVariable("id") int id, @RequestBody @Valid PublicationDTO publicationDTO,
                                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new PublicationNotSavedException(bindingResult.getFieldErrors() + " bad name ");
        publicationService.updatePublication(id, convertToPublication(publicationDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private PublicationDTO convertToPublicationDTO(Publication publication) {
        return modelMapper.map(publication, PublicationDTO.class);
    }

    private Publication convertToPublication(PublicationDTO publicationDTO) {
        return modelMapper.map(publicationDTO, Publication.class);
    }


}
