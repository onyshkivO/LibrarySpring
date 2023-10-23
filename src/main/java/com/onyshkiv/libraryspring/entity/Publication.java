package com.onyshkiv.libraryspring.entity;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@ToString(of={"id","name"})
@EqualsAndHashCode(of={"id"})
@Table(name="publication")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @JsonView(Views.Id.class)
    private int id;
    @Column(name = "name",nullable = false)
    @NotBlank(message = "Bad publication name")
    @JsonView(Views.IdName.class)
    private String name;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "publication")
    @JsonView(Views.FullPublication.class)
    private Set<Book> books;

}

