package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@ToString(of={"id"})
@EqualsAndHashCode(of={"id"})
@Table(name = "active_book")
public class ActiveBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView(Views.Id.class)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    @JsonView(Views.IdName.class)
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.IdName.class)
    private LocalDate startDate;


    @Column(name = "end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.IdName.class)
    private Date endDate;

    @Column(name = "fine")
    @JsonView(Views.IdName.class)
    private Double fine;

    @ManyToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login")
    @JsonView(Views.FullActiveBook.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn")
    @JsonView(Views.IdName.class)
    private Book book;
}
