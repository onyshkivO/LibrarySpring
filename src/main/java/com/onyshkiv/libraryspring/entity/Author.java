package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authors_id")
    private int authorId;

    @Column(name = "name")
    @NotBlank(message = "Bad author name")
    private String name;

    @ManyToMany
    @JoinTable(name = "book_has_authors",
            joinColumns = @JoinColumn(name = "a_id", referencedColumnName = "authors_id"),
            inverseJoinColumns = @JoinColumn(name = "b_isbn", referencedColumnName = "isbn"))
    @JsonBackReference("bookAuthors")
    private List<Book> books;

}
