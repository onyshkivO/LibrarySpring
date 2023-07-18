package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> getBooksByAuthorsAuthorId(int id);

    List<Book> getBooksByPublicationPublicationId(int id);

    List<Book> getBooksByNameStartingWith(String name);

}