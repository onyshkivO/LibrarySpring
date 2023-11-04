package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.dto.AuthenticationRequestDto;
import com.onyshkiv.libraryspring.dto.AuthenticationResponseDto;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.JwtException;
import com.onyshkiv.libraryspring.service.UserService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationProvider authenticationProvider;
    private final UserService userService;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationProvider authenticationProvider, UserService userService, JwtUtil jwtUtil) {
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
        this.jwtUtil = jwtUtil;

    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto req) {

        try {
            Authentication authentication =
                    authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()));
            User user = userService.getUserByLogin(req.getLogin());
            String token = jwtUtil.createToken(user);
            AuthenticationResponseDto loginRes = new AuthenticationResponseDto(token);
            return ResponseEntity.ok(loginRes);
        } catch (BadCredentialsException e) {
            throw new JwtException("Invalid username or password");
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }
}