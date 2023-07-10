package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.DTO.PublicationDTO;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotSavedException;
import com.onyshkiv.libraryspring.service.PublicationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<PublicationDTO>> getAllPublications() {
        List<PublicationDTO> publications = publicationService.getAllPublications()
                .stream()
                .map(this::convertToPublicationDTO)
                .toList();
        return new ResponseEntity<>(publications, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublicationById(@PathVariable("id") int id) {
        PublicationDTO publication = convertToPublicationDTO(publicationService.getPublicationById(id));
        return new ResponseEntity<>(publication, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<PublicationDTO> createPublication(@RequestBody @Valid PublicationDTO publicationDTO,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new PublicationNotSavedException(bindingResult.getFieldErrors() + " bad name ");
        Publication publication = publicationService.savePublication(convertToPublication(publicationDTO));
        return new ResponseEntity<>(convertToPublicationDTO(publication), HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PublicationDTO> updatePublication(@PathVariable("id") int id, @RequestBody @Valid PublicationDTO publicationDTO,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            throw new PublicationNotSavedException(bindingResult.getFieldErrors() + " bad name ");

        Publication publication = publicationService.updatePublication(id, convertToPublication(publicationDTO));
        return new ResponseEntity<>(convertToPublicationDTO(publication), HttpStatus.OK);
    }


    private PublicationDTO convertToPublicationDTO(Publication publication) {
        return modelMapper.map(publication, PublicationDTO.class);
    }

    private Publication convertToPublication(PublicationDTO publicationDTO) {
        return modelMapper.map(publicationDTO, Publication.class);
    }


}
