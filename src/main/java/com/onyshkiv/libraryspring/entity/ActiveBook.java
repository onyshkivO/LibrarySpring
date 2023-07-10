package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name = "subscription_status_id")
    private SubscriptionStatus subscriptionStatus;

    @Temporal(TemporalType.DATE)
    //    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    //    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "fine")
    private Double fine;

    @ManyToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login")
   @JsonBackReference("userActiveBook")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn")
    //@JsonManagedReference("bookActiveBook")
    @JsonIgnoreProperties("activeBooks")
    private Book book;
}
