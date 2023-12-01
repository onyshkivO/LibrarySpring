package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.User;
import com.onyshkiv.libraryspring.entity.UserStatus;
import com.onyshkiv.libraryspring.exception.user.UserNotFoundException;
import com.onyshkiv.libraryspring.repository.UserRepository;
import com.onyshkiv.libraryspring.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebMvcTest(UserService.class)
public class UserServiceTest {
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private MyUserDetailsService myUserDetailsService;
    @InjectMocks
    @Autowired
    private UserService userService;

    @Test
    public void getUserByLoginWhenExistTest() {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        when(userRepository.findById(anyString()))
                .thenReturn(Optional.of(user));

        user = userService.getUserByLogin(anyString());
        assertThat(user.getLogin()).isEqualTo("user");
        assertThat(user.getEmail()).isEqualTo("user@gmail.com");
        assertThat(user.getPassword()).isEqualTo("123");
        verify(userRepository).findById(anyString());
    }

    @Test
    public void getUserByLoginWhenNotExistTest() {
        when(userRepository.findById(anyString()))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserByLogin(anyString()))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getUsersTest() {
        User user1 = User.builder()
                .login("user1")
                .email("user1@gmail.com")
                .password("111")
                .build();
        User user2 = User.builder()
                .login("user2")
                .email("user2@gmail.com")
                .password("222")
                .build();
        User user3 = User.builder()
                .login("user3")
                .email("user3@gmail.com")
                .password("333")
                .build();

        Page<User> userPage = new PageImpl<>(List.of(
                user1, user2, user3
        ));
        when(userRepository.findAll((Pageable) any()))
                .thenReturn(userPage);

        PageRequest pageable = PageRequest.of(0, 10);

        DataPageDto<User> userPageResult = userService.getAllUsers(pageable);
        List<User> users = userPageResult.getData();
        assertThat(users.get(0).getLogin()).isEqualTo("user2");
        assertThat(users.get(0).getEmail()).isEqualTo("user1@gmail.com");
        assertThat(users.get(0).getPassword()).isEqualTo("111");
        assertThat(users.get(1).getLogin()).isEqualTo("user2");
        assertThat(users.get(1).getEmail()).isEqualTo("user2@gmail.com");
        assertThat(users.get(1).getPassword()).isEqualTo("222");
        assertThat(users.get(2).getLogin()).isEqualTo("user3");
        assertThat(users.get(2).getEmail()).isEqualTo("user3@gmail.com");
        assertThat(users.get(2).getPassword()).isEqualTo("333");
        verify(userRepository).findAll(pageable);
    }

    @Test
    public void saveUserTest() {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .password("123")
                .build();
        when(passwordEncoder.encode(anyString())).thenReturn("hashed password");
        when(userRepository.save(any())).thenReturn(user);

        user = userService.saveUser(user, Role.ROLE_READER);
        assertThat(user.getLogin()).isEqualTo("user");
        assertThat(user.getEmail()).isEqualTo("user@gmail.com");
        assertThat(user.getPassword()).isEqualTo("hashed password");
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getRole()).isEqualTo(Role.ROLE_READER);

        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void updateUserTest() {
        User userFromDb = User.builder()
                .login("user")
                .email("user@gmail.com")
                .firstName("name")
                .lastName("name")
                .phone("123")
                .password("123")
                .build();
        User user = User.builder()
                .login("not copied")
                .email("user(updated)@gmail.com")
                .firstName("name(updated)")
                .lastName("name(updated)")
                .phone("123(updated)")
                .password("not copied")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(userFromDb));
        userService.updateUser("user", user);
        assertThat(userFromDb.getLogin()).isEqualTo("user");
        assertThat(userFromDb.getEmail()).isEqualTo("user(updated)@gmail.com");
        assertThat(userFromDb.getPassword()).isEqualTo("123");
        assertThat(userFromDb.getFirstName()).isEqualTo("name(updated)");
        assertThat(userFromDb.getLastName()).isEqualTo("name(updated)");
        assertThat(userFromDb.getPhone()).isEqualTo("123(updated)");

//        verify(userRepository, times(1)).save(any()); //я то тестую чи можна забрати
        verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    public void changeUserStatusTest() {
        User user = User.builder()
                .login("user")
                .email("user@gmail.com")
                .firstName("name")
                .lastName("name")
                .phone("123")
                .password("123")
                .userStatus(UserStatus.ACTIVE)
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        userService.changeUserStatus(anyString());
        assertThat(user.getUserStatus()).isEqualTo(UserStatus.BLOCKED);
        verify(userRepository, times(1)).findById(anyString());
    }

    @Test
    public void deleteUser() {
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));
        userService.delete(anyString());
        verify(userRepository, times(1)).delete(any());
        verify(userRepository, times(1)).findById(anyString());
    }


}
