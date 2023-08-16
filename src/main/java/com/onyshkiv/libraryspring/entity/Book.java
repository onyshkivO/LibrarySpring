package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = {"isbn","name"})
@Entity
@Table(name = "book")
public class Book {
    @Id
    @Column(name = "isbn",length = 30)
    @NotNull(message = "Bad book isbn")
    @Pattern(regexp = "^(?=(?:\\D?\\d){10}(?:(?:\\D?\\d){3})?$)[\\d-]+?$",message = "Bad book isbn")
    private String isbn;

    @Column(name = "name")
    @NotBlank(message = "Bad book name")
    @NotNull(message = "Bad book name")
    private String name;

    @Column(name = "date_of_publication")
    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfPublication;

    @Column(name = "quantity")
    @NotNull(message = "Bad quantity value")
    private Integer quantity;

    @Column(name = "details",length = 1000)
    private String details;

    @OneToMany(mappedBy = "book")
    @JsonIgnoreProperties("book")
    private List<ActiveBook> activeBooks;


    @ManyToOne
    @JoinColumn(name = "publication_id", referencedColumnName = "publication_id")
    //@JsonManagedReference("bookPublication")
    private Publication publication;

    @ManyToMany(fetch = FetchType.EAGER,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_has_authors",
            joinColumns = @JoinColumn(name = "b_isbn",referencedColumnName = "isbn"),
            inverseJoinColumns = @JoinColumn(name = "a_id",referencedColumnName = "authors_id"))
    //@JsonManagedReference("bookAuthors")
    private List<Author> authors;

}
