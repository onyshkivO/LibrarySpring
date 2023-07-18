package com.onyshkiv.libraryspring.controller;


import com.onyshkiv.libraryspring.DTO.UserDTO;
import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.exception.user.UserNotSavedException;
import com.onyshkiv.libraryspring.service.UserService;
import com.onyshkiv.libraryspring.util.UserValidator;
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
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, UserValidator userValidator) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.userValidator = userValidator;
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers()
                .stream()
                .map(this::convertToUserDTO)
                .toList();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{login}")
    public ResponseEntity<UserDTO> getUserByLogin(@PathVariable("login") String login) {
        Optional<User> optionalUser = userService.getUserByLogin(login);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException("Not user found with login " + login);//todo можливо це має робитися через aop і ті advice
        return new ResponseEntity<>(convertToUserDTO(optionalUser.get()), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<UserDTO> saveUser(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        userValidator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());
        User user = userService.saveUser(convertAToUser(userDTO), Role.ROLE_READER);
        return new ResponseEntity<>(convertToUserDTO(user), HttpStatus.OK);
    }

    @PostMapping("/librarian")
    public ResponseEntity<UserDTO> createLibrarian(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {
        userValidator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());
        User user = userService.saveUser(convertAToUser(userDTO), Role.ROLE_LIBRARIAN);
        return new ResponseEntity<>(convertToUserDTO(user), HttpStatus.OK);
    }


    @PatchMapping("/{login}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("login") String login, @RequestBody @Valid UserDTO userDTO,
                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());
        User user = userService.updateUser(login, convertAToUser(userDTO));
        return new ResponseEntity<>(convertToUserDTO(user), HttpStatus.OK);
    }

    @DeleteMapping("/{login}")
    public ResponseEntity<UserDTO> deleteUserByLogin(@PathVariable("login") String login) {
        User user = userService.deleteUserByLogin(login);
        return new ResponseEntity<>(convertToUserDTO(user), HttpStatus.OK);
    }

    @PatchMapping("/status/{login}")
    public ResponseEntity<UserDTO> changeUserStatus(@PathVariable("login") String login) {
        User user = userService.changeUserStatus(login);
        return new ResponseEntity<>(convertToUserDTO(user), HttpStatus.OK);

    }

    private UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    private User convertAToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}
