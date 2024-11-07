package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.*;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.exception.user.UserNotSavedException;
import com.onyshkiv.libraryspring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Cacheable(key = "#login", value = "User")
    public Optional<User> getUserByLogin(String login) {
        return userRepository.findById(login);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User saveUser(User user, Role role) {
        Optional<User> optionalUser = userRepository.findById(user.getLogin());
        if (optionalUser.isPresent() || user.getLogin().isBlank())
            throw new UserNotSavedException("User with login " + user.getLogin() + " already exist");// можливо зайве, бо є валідатор
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setUserStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(key = "#login", value = "User")
    public User updateUser(String login, User user) {
        Optional<User> optionalUser = userRepository.findById(login);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException("Not user found with login " + login);
        //todo якась хєрня
        if (!passwordEncoder.matches(user.getPassword(), optionalUser.get().getPassword()))
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null)
            user.setRole(optionalUser.get().getRole());
        if (user.getUserStatus() == null)
            user.setUserStatus(optionalUser.get().getUserStatus());
        return userRepository.save(user);
    }

    @Transactional
    @CacheEvict(key = "#login", value = "User")
    public User deleteUserByLogin(String login) {
        Optional<User> optionalUser = userRepository.findById(login);
        if (optionalUser.isEmpty())
            throw new UserNotFoundException("Not user found with login " + login);
        userRepository.deleteById(login);
        return optionalUser.get();
    }

    @Transactional
    @CacheEvict(key = "#login", value = "User")
    public User changeUserStatus(String login){
        Optional<User> optionalUser = userRepository.findById(login);
        if (optionalUser.isEmpty()) throw new UserNotFoundException("There are not user with login "+ login);
        User user = optionalUser.get();
        if (user.getUserStatus().equals(UserStatus.ACTIVE)) {
            user.setUserStatus(UserStatus.BLOCKED);
        } else {
            user.setUserStatus(UserStatus.ACTIVE);
        }
        return user;


    }


}
