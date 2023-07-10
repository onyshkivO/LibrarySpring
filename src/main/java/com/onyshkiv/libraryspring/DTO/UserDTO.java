package com.onyshkiv.libraryspring.DTO;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Role;
import com.onyshkiv.libraryspring.entity.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDTO {
    @NotBlank(message = "Bad user login")
    @NotNull(message = "Bad user login")
    @Pattern(regexp = "^(?=.{4,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$", message = "Bad user login")
    private String login;

    @NotBlank(message = "Bad email")
    @Email(message = "Bad email")
    private String email;

    @NotBlank(message = "Bad password")
    @NotNull(message = "Bad password")
    @Pattern(regexp = "^[A-Za-z0-9_-]{6,18}$", message = "Bad password")
    private String password;

    @NotBlank(message = "Bad First Name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\\\s]{2,20}$", message = "Bad First Name")
    private String firstName;

    @NotBlank(message = "Bad Last Name")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\\\s]{2,20}$", message = "Bad Last Name")
    private String lastName;

    @Pattern(regexp = "(?:\\+38)?(?:\\(0[0-9]{2}\\)[ .-]?[0-9]{3}[ .-]?[0-9]{2}[ .-]?[0-9]{2}|0[0-9]{2}[ .-]?[0-9]{3}[ .-]?[0-9]{2}[ .-]?[0-9]{2}|0[0-9]{2}[0-9]{7})$",
            message = "Bad phone number")
    private String phone;


//todo хз

//    @Enumerated(EnumType.ORDINAL)
//    @Column(name = "role_id")
//    private Role role;
//
//    @Enumerated(EnumType.ORDINAL)
//    @Column(name = "status_id")
//    private UserStatus userStatus;

    private List<ActiveBook> activeBooks;


}
