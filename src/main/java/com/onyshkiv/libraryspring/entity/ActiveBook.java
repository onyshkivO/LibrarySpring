package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "active_book")
public class ActiveBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView(Views.Id.class)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    @JsonView(Views.Full.class)
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "start_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.Full.class)
    private LocalDate startDate;


    @Column(name = "end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.Full.class)
    private Date endDate;

    @Column(name = "fine")
    @JsonView(Views.Full.class)
    private Double fine;

    @ManyToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login")
    @JsonView(Views.Full.class)
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn")
    @JsonView(Views.Full.class)
    private Book book;
}
