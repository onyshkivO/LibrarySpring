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
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(of = {"isbn","name"})
@Entity
@Table(name = "book")
@JsonIdentityInfo(
        property = "id",
        generator = ObjectIdGenerators.PropertyGenerator.class
)
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
    @JsonView(Views.Full.class)
    private LocalDate dateOfPublication;

    @Column(name = "quantity")
    @NotNull(message = "Bad quantity value")
    @JsonView(Views.Full.class)
    private Integer quantity;



    @Column(name = "details")
    @JsonView(Views.Full.class)
    private String details;

    //todo признгачити activeBooks = new ArrayList<>(); щоб не було проблем
    @OneToMany(mappedBy = "book")
    @JsonView(Views.Full.class)
    private List<ActiveBook> activeBooks;


    @ManyToOne
    @JoinColumn(name = "publication_id", referencedColumnName = "publication_id")
    @JsonView(Views.Full.class)
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_has_authors",
            joinColumns = @JoinColumn(name = "b_isbn",referencedColumnName = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "a_id",referencedColumnName = "authors_id"))
    @JsonView(Views.Full.class)
    private List<Author> authors;

}
