package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@ToString(of = {"isbn","name"})
@EqualsAndHashCode(of = {"isbn"})
@Entity
@Table(name = "book")
//@JsonIdentityInfo(
//        property = "isbn",
//        generator = ObjectIdGenerators.PropertyGenerator.class
//)
public class Book {
    @Id
    @Column(name = "isbn")
    @NotNull(message = "Bad book isbn")
    @Pattern(regexp = "^(?=(?:\\D?\\d){10}(?:(?:\\D?\\d){3})?$)[\\d-]+?$",message = "Bad book isbn")
    @JsonView(Views.Id.class)
    private String isbn;

    @Column(name = "name")
    @NotBlank(message = "Bad book name")
    @NotNull(message = "Bad book name")
    @JsonView(Views.IdName.class)
    private String name;

    @Column(name = "date_of_publication")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonView(Views.FullBook.class)
    private LocalDate dateOfPublication;

    @Column(name = "quantity")
    @NotNull(message = "Bad quantity value")
    @JsonView(Views.FullBook.class)
    private Integer quantity;



    @Column(name = "details")
    @JsonView(Views.FullBook.class)
    private String details;

    @OneToMany(mappedBy = "book")
    @JsonView(Views.Full.class)
    private Set<ActiveBook> activeBooks;

    @ManyToOne
    @JoinColumn(name = "publication_id", referencedColumnName = "id",nullable = false)
    @JsonView(Views.FullBook.class)
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_has_authors",
            joinColumns = @JoinColumn(name = "b_isbn",referencedColumnName = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "a_id",referencedColumnName = "id"))
    @JsonView(Views.FullBook.class)
    private Set<Author> authors = new HashSet<>();

}
