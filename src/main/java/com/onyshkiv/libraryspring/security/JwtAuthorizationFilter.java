package com.onyshkiv.libraryspring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.entity.UserStatus;
import com.onyshkiv.libraryspring.exception.JwtException;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import com.onyshkiv.libraryspring.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MyUserDetailsService userDetailsService;
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper;

    @Autowired
    public JwtAuthorizationFilter(JwtUtil jwtUtil, MyUserDetailsService userDetailsService, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = parseJwt(request);
            if (jwtToken != null && jwtUtil.validateJwtToken(jwtToken)) {
                String userLogin = jwtUtil.extractUsername(jwtToken);
                if (userLogin != null) { //SecurityContextHolder.getContext().getAuthentication() == null значть користувач не залогінений
                    if (jwtUtil.isValidUser(jwtToken, userLogin)) {
                        //todo тут зробити, щоб краще отримувати User
                        User user = createUser(jwtUtil.extractClaim(jwtToken, claims -> claims.get("user", LinkedHashMap.class)));
                        MyUserDetails userDetails = new MyUserDetails(user);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }

                }
            }
        } catch (Exception e) {

            System.out.println(e.getMessage());
            //throw new JwtException(e.getMessage());
        }
        filterChain.doFilter(request, response);

       /* try {
            String jwtToken = parseJwt(request);
            if (jwtToken != null&& jwtUtil.validateJwtToken(jwtToken)) {
                String userLogin = jwtUtil.extractUsername(jwtToken);
                if (userLogin != null && SecurityContextHolder.getContext().getAuthentication() == null) { //SecurityContextHolder.getContext().getAuthentication() == null значть користувач не залогінений
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userLogin);
                    if (jwtUtil.isValidUser(jwtToken, userDetails)) {

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        userDetails.getPassword(),
                                        userDetails.getAuthorities()
                                );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }

                }
            }
        } catch (Exception e) {

            System.out.println(e.getMessage());
            //throw new JwtException(e.getMessage());
        }
        filterChain.doFilter(request, response);*/
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader(TOKEN_HEADER);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
            return headerAuth.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    private User createUser(Map<String, Object> userMap) {
        return User.builder()
                .login(userMap.get("login").toString())
                .email(userMap.get("email").toString())
                .phone(userMap.get("phone").toString())
                .firstName(userMap.get("firstName").toString())
                .lastName(userMap.get("lastName").toString())
                .role(Role.valueOf(userMap.get("role").toString()))
                .userStatus(UserStatus.valueOf(userMap.get("userStatus").toString()))
                .build();
    }
}