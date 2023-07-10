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
@Table(name="publication")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publication_id")
    private int publicationId;
    @Column(name = "name")
    @NotBlank(message = "Bad publication name")
    private String name;

    @OneToMany(mappedBy = "publication")
    @JsonBackReference("bookPublication")
    private List<Book> books;

}

