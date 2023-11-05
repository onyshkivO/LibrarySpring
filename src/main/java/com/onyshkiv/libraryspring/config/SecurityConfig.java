package com.onyshkiv.libraryspring.config;

import com.onyshkiv.libraryspring.security.JwtAuthorizationFilter;
import com.onyshkiv.libraryspring.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
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
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    public SecurityConfig(MyUserDetailsService userDetailsService, JwtAuthorizationFilter jwtAuthorizationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        return http
//                .csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(requests -> requests
//                        .requestMatchers(HttpMethod.GET, "/books").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
//                        .requestMatchers(HttpMethod.PUT, "/activeBooks/return/{id}", "/activeBooks/{id}"
//                                , "/authors/{id}", "/books/{isbn}", "/publications/{id}", "/users/{login}", "/users/status/{login}").hasAnyRole("LIBRARIAN", "ADMINISTRATOR")
//                        .requestMatchers(HttpMethod.PUT, "/users/status/{login}").hasRole("ADMINISTRATOR")
//                        .requestMatchers(HttpMethod.POST, "/authors", "/books", "/publications", "/users/librarian").hasRole("ADMINISTRATOR")
//                        .requestMatchers(HttpMethod.DELETE, "/activeBooks/{id}").hasAnyRole("LIBRARIAN","ADMINISTRATOR")
//                        .requestMatchers(HttpMethod.DELETE,  "/authors/{id}", "/publications/{id}", "/users/{login}", "/books/{isbn}").hasRole("ADMINISTRATOR")
//                        .anyRequest().authenticated()
//                )
//                .authenticationProvider(authenticationProvider())
//                .httpBasic(Customizer.withDefaults())
////                .formLogin(AbstractHttpConfigurer::disable)
//                .build();
//    }

    //todo додати ролі
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.
                csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/books").permitAll()
                        .requestMatchers(HttpMethod.GET, "/authors").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/activeBooks/return/{id}", "/activeBooks/{id}"
                                , "/authors/{id}", "/books/{isbn}", "/publications/{id}", "/users/{login}", "/users/status/{login}").hasAnyRole("LIBRARIAN", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.PUT, "/users/status/{login}").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.POST, "/authors", "/books", "/publications", "/users/librarian").hasRole("ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/activeBooks/{id}").hasAnyRole("LIBRARIAN", "ADMINISTRATOR")
                        .requestMatchers(HttpMethod.DELETE, "/authors/{id}", "/publications/{id}", "/users/{login}", "/books/{isbn}").hasRole("ADMINISTRATOR")
                        .anyRequest().authenticated()
                )
                .sessionManagement(manager -> manager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
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
