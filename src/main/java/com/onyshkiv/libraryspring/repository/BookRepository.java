package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {

    @Query(value = "SELECT b from Book b left join fetch b.authors left join fetch b.publication")
    Page<Book> findAll(Pageable pageable);

    List<Book> getBooksByAuthorsAuthorId(int id);

    List<Book> getBooksByPublicationPublicationId(int id);

    List<Book> getBooksByNameStartingWith(String name);

}