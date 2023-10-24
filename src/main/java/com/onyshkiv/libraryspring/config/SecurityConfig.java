package com.onyshkiv.libraryspring.config;

import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final MyUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.GET, "/books").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/activeBooks/return/{id}", "/activeBooks/{id}"
                                , "/authors/{id}", "/books/{isbn}", "/publications/{id}", "/users/{login}", "/users/status/{login}").hasAnyRole("LIBRARIAN", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/users/status/{login}").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/authors", "/books", "/publications", "/users/librarian").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/activeBooks/{id}").hasAnyRole("LIBRARIAN","ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE,  "/authors/{id}", "/publications/{id}", "/users/{login}", "/books/{isbn}").hasRole("ADMINISTRATOR")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .httpBasic(Customizer.withDefaults())
//                .formLogin(AbstractHttpConfigurer::disable)
                .build();
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(encoder());
        return authenticationProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
