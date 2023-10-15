package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Integer> {
    @EntityGraph(attributePaths = {"books"})
    Page<Author> findAll(Pageable pageable);
}
