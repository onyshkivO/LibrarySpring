package com.onyshkiv.libraryspring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "active_book")
public class ActiveBook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "active_book_id")
    private int activeBookId;

    @Enumerated(EnumType.ORDINAL)
    private SubscriptionStatus subscriptionStatus;

    @Temporal(TemporalType.DATE)
    //    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    //    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date endDate;

    @Column(name = "fine")
    private Double fine;

    @ManyToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn")
    private Book book;
}
