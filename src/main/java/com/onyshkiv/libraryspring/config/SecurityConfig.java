package com.onyshkiv.libraryspring.config;

import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final MyUserDetailsService userDetailsService;
    private final JWTFilter jwtAuthFilter;

    @Autowired
    public SecurityConfig(MyUserDetailsService userDetailsService, JWTFilter jwtAuthFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.GET,"/books").hasAnyRole("ADMINISTRATOR", "READER", "LIBRARIAN")
                        .requestMatchers(HttpMethod.PATCH, "/activeBooks/return/{id}", "/activeBooks/{id}"
                                , "/authors/{id}", "/books/{isbn}", "/publications/{id}", "/users/{login}", "/users/status/{login}").hasAnyRole("LIBRARIAN", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PATCH, "/users/status/{login}").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/authors", "/books", "/publications", "/users/librarian").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/activeBooks/{id}").hasRole("LIBRARIAN")
                        .requestMatchers(HttpMethod.DELETE, "/activeBooks/{id}", "/authors/{id}", "/publications/{id}", "/users/{login}", "/books/{isbn}").hasRole("ADMINISTRATOR")
                        //.anyRequest().hasAnyRole("ADMINISTRATOR", "USER", "LIBRARIAN")
                        .anyRequest().authenticated()
                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
                .authenticationProvider(authenticationProvider())
                //.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/success")
//                        .permitAll()
//                )
                .httpBasic(Customizer.withDefaults())
                //.formLogin(AbstractHttpConfigurer::disable)
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
