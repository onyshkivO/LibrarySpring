package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.entity.UserStatus;
import com.onyshkiv.libraryspring.exception.author.AuthorNotFoundException;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public User getUserByLogin(String login) {
        return userRepository.findById(login)
                .orElseThrow(() -> new UserNotFoundException("there are not user with login " + login));
    }

    public DataPageDto<User> getAllUsers(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);
        return new DataPageDto<>(usersPage.getContent(),pageable.getPageNumber(),usersPage.getTotalPages());
    }

    @Transactional
    public User saveUser(User user, Role role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setUserStatus(UserStatus.ACTIVE);
        return userRepository.save(user);
    }

    //todo метод для зміни паролю
    @Transactional
    public User updateUser(String login, User user) {
        User userFromDb = getUserByLogin(login);
        BeanUtils.copyProperties(user, userFromDb, "login","password","role","userStatus","activeBooks");
        return userFromDb;
//        return userRepository.save(userFromDb);//todo перевірити чи буде працювати якщо це забрати
    }

    @Transactional
    public User changeUserStatus(String login){
        User user = getUserByLogin(login);
        if (user.getUserStatus().equals(UserStatus.ACTIVE)) {
            user.setUserStatus(UserStatus.BLOCKED);
        } else {
            user.setUserStatus(UserStatus.ACTIVE);
        }
        return user;


    }

    @Transactional
    public void delete(String login) {
        User user = getUserByLogin(login);
        userRepository.delete(user);
    }
}
