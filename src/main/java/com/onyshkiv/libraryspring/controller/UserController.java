package com.onyshkiv.libraryspring.controller;


import com.fasterxml.jackson.annotation.JsonView;
import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.entity.Views;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.exception.user.UserNotSavedException;
import com.onyshkiv.libraryspring.service.UserService;
import com.onyshkiv.libraryspring.util.UserValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }

    @GetMapping()
    @JsonView(Views.FullUser.class)
    public ResponseEntity<DataPageDto<User>> getAllUsers(@PageableDefault(sort = {"login"}, direction = Sort.Direction.DESC) Pageable pageable) {
        DataPageDto<User> users = userService.getAllUsers(pageable);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{login}")
    @JsonView(Views.FullUser.class)
    public ResponseEntity<User> getUserByLogin(@PathVariable("login") User user) {
//        Optional<User> optionalUser = userService.getUserByLogin(login);
//        if (optionalUser.isEmpty())
//            throw new UserNotFoundException("Not user found with login " + login);//todo можливо це має робитися через aop і ті advice

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

//    @GetMapping("/{login}")
//    @JsonView(Views.FullUser.class)
//    public ResponseEntity<User> getUserByLogin(@PathVariable("login") String login) {
//        Optional<User> optionalUser = userService.getUserByLogin(login);
//        if (optionalUser.isEmpty())
//            throw new UserNotFoundException("Not user found with login " + login);//todo можливо це має робитися через aop і ті advice
//
//
//        return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
//    }
    @PostMapping()
    @JsonView(Views.IdName.class)
    public ResponseEntity<User> saveUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());

        User savedUser = userService.saveUser(user, Role.ROLE_READER);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @PostMapping("/librarian")
    @JsonView(Views.IdName.class)
    public ResponseEntity<User> createLibrarian(@RequestBody @Valid User user, BindingResult bindingResult) {
        userValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());

        User savedUser = userService.saveUser(user, Role.ROLE_LIBRARIAN);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }


    @PutMapping("/{login}")
    @JsonView(Views.FullUser.class)
    public ResponseEntity<User> updateUser(@PathVariable("login") User userFromDb,
                                           @RequestBody @Valid User user,
                                           BindingResult bindingResult) {
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());
        User updatedUser = userService.updateUser(userFromDb, user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{login}")
    @JsonView(Views.IdName.class)
    public void deleteUserByLogin(@PathVariable("login") User user) {
        userService.delete(user);
    }

    @PatchMapping("/status/{login}")
    public ResponseEntity<User> changeUserStatus(@PathVariable("login") User user) {
        User updatedUser = userService.changeUserStatus(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);

    }

}
