package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
//todo чому тут так багато запитів, адже publication то навіть не лист, то чому і в інших так, як це пофіксити
    @EntityGraph(attributePaths = {"authors","publication"})
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> getBooksByAuthorsId(int id, Pageable pageable);

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> getBooksByPublicationId(int id, Pageable pageable);

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> getBooksByNameStartingWith(String name, Pageable pageable);

}