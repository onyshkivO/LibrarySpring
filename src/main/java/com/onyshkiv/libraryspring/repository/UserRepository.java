package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, String> {
    @EntityGraph(attributePaths = {"activeBooks"})
    Page<User> findAll(Pageable pageable);

}
