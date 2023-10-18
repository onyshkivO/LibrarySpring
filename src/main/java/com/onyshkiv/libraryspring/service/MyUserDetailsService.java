package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.repository.UserRepository;
import com.onyshkiv.libraryspring.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        //todo чому при будь-якому запиті це перевіряється і запрошується до бд(иоже так і має бути)
        return new MyUserDetails(userRepository.findById(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found")));

    }
}
