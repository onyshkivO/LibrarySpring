package com.onyshkiv.libraryspring.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.dto.AuthenticationRequestDto;
import com.onyshkiv.libraryspring.dto.AuthenticationResponseDto;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.exception.JwtException;
import com.onyshkiv.libraryspring.security.MyUserDetails;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.service.UserService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private JwtUtil jwtUtil;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

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
            MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(req.getLogin());
//            Map<String, Object> claims = Map.of("role",userDetails.getUser().getRole());
            Map<String, Object> claims = Map.of("user",userDetails.getUser());
            String token = jwtUtil.generateToken(claims,userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            AuthenticationResponseDto loginRes = new AuthenticationResponseDto(token, refreshToken);

            return ResponseEntity.ok(loginRes);
        } catch (BadCredentialsException e) {
            throw new JwtException("Invalid username or password");
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }



    @GetMapping("/claims")
    public Object getClaims(Authentication authentication, @AuthenticationPrincipal MyUserDetails userDetails){
        SecurityContext context = SecurityContextHolder.getContext();
        return userDetails;

    }

    @PostMapping(value = "/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {

        try {
            String refreshToken = parseJwt(request);
            if (refreshToken != null && jwtUtil.validateJwtToken(refreshToken)) {
                String userLogin = jwtUtil.extractUsername(refreshToken);
                if (userLogin != null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userLogin);
                    if (jwtUtil.isValidUser(refreshToken, userDetails.getUsername())) {
                        String accesToken = jwtUtil.generateToken(userDetails);
                        AuthenticationResponseDto authResponse =
                                new AuthenticationResponseDto(accesToken, refreshToken);
                        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
                    }

                }
            }
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
            return headerAuth.substring(TOKEN_PREFIX.length());
        }
        return null;
    }




}