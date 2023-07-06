package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author,Integer> {
}
