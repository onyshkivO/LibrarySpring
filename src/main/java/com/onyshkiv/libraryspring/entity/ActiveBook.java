package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;


@Getter
@Setter
@Builder
@AllArgsConstructor
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
    @Column(name = "subscription_status",nullable = false)
    @JsonView(Views.IdName.class)
    @NotNull
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "start_date",nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.IdName.class)
    private LocalDate startDate;


    @Column(name = "end_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.IdName.class)
    private LocalDate endDate;

    @Column(name = "fine")
    @JsonView(Views.IdName.class)
    private Double fine;

    @ManyToOne
    @JoinColumn(name = "user_login", referencedColumnName = "login",nullable = false)
    @JsonView(Views.FullActiveBook.class)
    //@NotNull //todo попробувати з цим
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn",nullable = false)
    @JsonView(Views.IdName.class)
   // @NotNull
    private Book book;

    public ActiveBook(int id) {
        this.id = id;
    }
}
