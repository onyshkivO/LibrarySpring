package com.onyshkiv.libraryspring.auth;


import com.onyshkiv.libraryspring.DTO.UserDTO;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.user.UserNotSavedException;
import com.onyshkiv.libraryspring.util.UserValidator;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authService;
    private final UserValidator userValidator;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthController(AuthenticationService authService, UserValidator userValidator, ModelMapper modelMapper) {
        this.authService = authService;
        this.userValidator = userValidator;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid UserDTO userDTO, BindingResult bindingResult) {//?????
        userValidator.validate(userDTO, bindingResult);
        if (bindingResult.hasErrors()) throw new UserNotSavedException(bindingResult.getFieldErrors().toString());
        return ResponseEntity.ok(authService.register(convertAToUser(userDTO)));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }


    private User convertAToUser(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }
}

