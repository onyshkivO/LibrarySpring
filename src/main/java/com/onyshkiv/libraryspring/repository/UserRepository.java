package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, String> {
    @Query("from User u " +
            "left join fetch u.activeBooks ac " +
            "left join fetch ac.book b " +
            "left join fetch b.publication " +
            "left join fetch b.authors "
    )
    Page<User> findAll(Pageable pageable);


}
