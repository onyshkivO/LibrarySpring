package com.onyshkiv.libraryspring.DTO;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Publication;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
public class BookDTO {

    @NotNull(message = "Bad book isbn")
    @Pattern(regexp = "^(?=(?:\\D?\\d){10}(?:(?:\\D?\\d){3})?$)[\\d-]+?$", message = "Bad book isbn")
    private String isbn;

    @NotBlank(message = "Bad book name")
    @NotNull(message = "Bad book name")
    private String name;

    @Temporal(TemporalType.DATE)
//    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dateOfPublication;

    @NotNull(message = "Bad quantity value")
    private Integer quantity;

    private String details;
    private Publication publication;//тут може дто але тоді воно думає, що такого немав бд
    private List<Author> authors;

}
