package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authors_id")
    @JsonView(Views.Id.class)
    private int id;

    @Column(name = "name")
    @NotBlank(message = "Bad author name")
    @JsonView(Views.IdName.class)
    private String name;

    @ManyToMany
    @JoinTable(name = "book_has_authors",
            joinColumns = @JoinColumn(name = "a_id", referencedColumnName = "authors_id"),
            inverseJoinColumns = @JoinColumn(name = "b_isbn", referencedColumnName = "isbn"))
    @JsonView(Views.Full.class)
    private List<Book> books;

}
