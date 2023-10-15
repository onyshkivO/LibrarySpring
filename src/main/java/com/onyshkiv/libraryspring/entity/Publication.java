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
@Table(name="publication")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publication_id")
    @JsonView(Views.Id.class)
    private int id;
    @Column(name = "name")
    @NotBlank(message = "Bad publication name")
    @JsonView(Views.IdName.class)
    private String name;

    @OneToMany(mappedBy = "publication")
    @JsonView(Views.Full.class)
    private List<Book> books;

}

