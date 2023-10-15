package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> getBooksByAuthorsAuthorId(int id, Pageable pageable);

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> getBooksByPublicationPublicationId(int id, Pageable pageable);

    @EntityGraph(attributePaths = {"authors","activeBooks"})
    Page<Book> getBooksByNameStartingWith(String name, Pageable pageable);

}