package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publication,Integer> {
    @EntityGraph(attributePaths = {"books"})
    Page<Publication> findAll(Pageable pageable);
}
