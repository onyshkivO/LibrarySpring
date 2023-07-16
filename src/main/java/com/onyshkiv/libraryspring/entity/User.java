package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
@Table(name = "user")
public class User{

    @Id
    @Column(name = "login")
    @NotBlank(message = "Bad user login")
    @NotNull(message = "Bad user login")
    @Pattern(regexp = "^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", message = "Bad user login")
    private String login;

    @Column(name = "email")
    @NotBlank(message = "Bad email")
    @Email(message = "Bad email")
    private String email;

    @Column(name = "password")
    @NotBlank(message = "Bad password1")
    @NotNull(message = "Bad password2")
    //@Pattern(regexp = "^[A-Za-z0-9_-]{6,18}$", message = "Bad password3")
    //todo пароль щоб не було видно при отриманні користувача
    private String password;

    @Column(name = "first_name")
    @NotBlank(message = "Bad First Name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\\\s]{2,20}$", message = "Bad First Name")
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Bad Last Name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\\\s]{2,20}$", message = "Bad Last Name")
    private String lastName;

    @Column(name = "phone")
    @Pattern(regexp = "(?:\\+38)?(?:\\(0[0-9]{2}\\)[ .-]?[0-9]{3}[ .-]?[0-9]{2}[ .-]?[0-9]{2}|0[0-9]{2}[ .-]?[0-9]{3}[ .-]?[0-9]{2}[ .-]?[0-9]{2}|0[0-9]{2}[0-9]{7})$",
            message = "Bad phone number")
    private String phone;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role_id")
    private Role role;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status_id")
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user",fetch = FetchType.EAGER)
    @JsonManagedReference("userActiveBook")
    private List<ActiveBook> activeBooks;

}
