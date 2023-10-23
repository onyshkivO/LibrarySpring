package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, String> {
//todo чому тут так багато запитів, адже publication то навіть не лист, то чому і в інших так, як це пофіксити
    @EntityGraph(attributePaths = {"authors","publication"})
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"authors","publication"})
    Optional<Book> findById(String id);

    @EntityGraph(attributePaths = {"authors","publication"})
    Page<Book> getBooksByAuthorsId(int id, Pageable pageable);

    @EntityGraph(attributePaths = {"authors","publication"})
    Page<Book> getBooksByPublicationId(int id, Pageable pageable);

    @EntityGraph(attributePaths = {"authors","publication"})
    Page<Book> getBooksByNameContainingIgnoreCase(String name, Pageable pageable);

    /*{
        "isbn": "1111111111",
            "name": "test book",
            "dateOfPublication": "1990-01-15",
            "quantity": 12,
            "details": "Test",
            "publication": {
        "id": 1
    }
    }*/

}