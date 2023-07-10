package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.DTO.ActiveBookDTO;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.service.ActiveBookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<ActiveBookDTO> getAllActiveBooks() {
        return activeBookService.getAllActiveBooks()
                .stream()
                .map(this::convertToActiveBookDTO)
                .collect(Collectors.toList());
    }



    private ActiveBookDTO convertToActiveBookDTO(ActiveBook activeBook) {
        return modelMapper.map(activeBook, ActiveBookDTO.class);
    }

}
