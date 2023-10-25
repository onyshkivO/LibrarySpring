package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@ToString(of={"login","email"})
@EqualsAndHashCode(of={"login"})
@Table(name = "user")
@JsonIdentityInfo(
        property = "login",
        generator = ObjectIdGenerators.PropertyGenerator.class
)
public class User{

    @Id
    @Column(name = "login")
    @NotBlank(message = "Bad user login")
    @NotNull(message = "Bad user login")
    @Pattern(regexp = "^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", message = "Bad user login")
    @JsonView(Views.Id.class)
    private String login;


    @Column(name = "email",nullable = false)
    @NotBlank(message = "Bad email")
    @Email(message = "Bad email")
    @JsonView(Views.IdName.class)
    private String email;
//to do user dto для паролю
    @Column(name = "password",nullable = false)
    @NotBlank(message = "Bad password1")
    @NotNull(message = "Bad password2")
    @Pattern(regexp = "^[$\\/A-Za-z0-9_-]{6,60}$", message = "Bad password3")
    private String password;

    @Column(name = "first_name",nullable = false)
    @NotBlank(message = "Bad First Name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\\\s]{2,20}$", message = "Bad First Name")
    @JsonView(Views.IdName.class)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    @NotBlank(message = "Bad Last Name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\\\s]{2,20}$", message = "Bad Last Name")
    @JsonView(Views.IdName.class)
    private String lastName;

    @Column(name = "phone")
    @Pattern(regexp = "(?:\\+38)?(?:\\(0[0-9]{2}\\)[ .-]?[0-9]{3}[ .-]?[0-9]{2}[ .-]?[0-9]{2}|0[0-9]{2}[ .-]?[0-9]{3}[ .-]?[0-9]{2}[ .-]?[0-9]{2}|0[0-9]{2}[0-9]{7})$",
            message = "Bad phone number")
    @JsonView(Views.IdName.class)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",nullable = false)
    private UserStatus userStatus;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    @JsonView(Views.FullUser.class)
    private Set<ActiveBook> activeBooks;

}
