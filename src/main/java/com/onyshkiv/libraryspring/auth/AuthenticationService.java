package com.onyshkiv.libraryspring.auth;

import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.repository.UserRepository;
import com.onyshkiv.libraryspring.security.JWTUtil;
import com.onyshkiv.libraryspring.security.MyUserDetails;
import com.onyshkiv.libraryspring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserService userService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthenticationService(UserService userService, UserRepository userRepository, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(User user) {
        User createdUser = userService.saveUser(user, Role.ROLE_READER);
        UserDetails userDetails = new MyUserDetails(createdUser);
        String jwtToken = jwtUtil.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword())
        );
        User user = userService.getUserByLogin(request.getLogin()).orElseThrow();
        UserDetails userDetails = new MyUserDetails(user);
        String jwtToken = jwtUtil.generateToken(userDetails);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }


}
