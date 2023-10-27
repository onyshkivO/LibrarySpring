package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ToString(of={"id","name"})
@EqualsAndHashCode(of={"id"})
@NoArgsConstructor
@Entity
@Table(name = "author")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView(Views.Id.class)
    private int id;

    @Column(name = "name")
    @NotBlank(message = "Bad author name")
    @JsonView(Views.IdName.class)
    private String name;

    @ManyToMany
    @JoinTable(name = "book_has_authors",
            joinColumns = @JoinColumn(name = "a_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "b_isbn", referencedColumnName = "isbn"))
    @JsonView(Views.FullAuthor.class)
    private Set<Book> books = new HashSet<>();

    public Author(int id, String name) {
        this.id=id;
        this.name=name;
    }

    public Author(String name) {
        this.name=name;
    }
}
