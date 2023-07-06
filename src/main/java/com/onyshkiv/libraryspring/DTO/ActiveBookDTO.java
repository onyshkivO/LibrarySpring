package com.onyshkiv.libraryspring.DTO;

import com.onyshkiv.libraryspring.entity.Book;
import com.onyshkiv.libraryspring.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ActiveBookDTO {

    @Temporal(TemporalType.DATE)
    //    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    private Double fine;

    private User user;

    private Book book;
}
