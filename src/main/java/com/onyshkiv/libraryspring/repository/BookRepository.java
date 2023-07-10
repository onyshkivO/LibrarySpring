package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
}