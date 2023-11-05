package com.onyshkiv.libraryspring.controller;

import com.onyshkiv.libraryspring.dto.AuthenticationRequestDto;
import com.onyshkiv.libraryspring.dto.AuthenticationResponseDto;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.JwtException;
import com.onyshkiv.libraryspring.security.MyUserDetails;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.service.UserService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, MyUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;

    }

    @PostMapping(value = "/login")
    public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto req) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(req.getLogin());

            String token = jwtUtil.generateToken(userDetails);
            AuthenticationResponseDto loginRes = new AuthenticationResponseDto(token);

            return ResponseEntity.ok(loginRes);
        } catch (BadCredentialsException e) {
            throw new JwtException("Invalid username or password");
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }
}