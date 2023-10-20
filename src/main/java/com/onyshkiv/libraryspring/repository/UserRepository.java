package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.entity.User;
import jakarta.validation.constraints.NotNull;
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
    @EntityGraph( type = EntityGraph.EntityGraphType.FETCH,attributePaths = {"activeBooks", "activeBooks.book", "activeBooks.book.authors", "activeBooks.book.publication"})
    Page<User> findAll(Pageable pageable);

    @EntityGraph( type = EntityGraph.EntityGraphType.FETCH,attributePaths = {"activeBooks", "activeBooks.book"})
    @NotNull
    Optional<User> findById(@NotNull String login);


    Optional<User> getUserByLogin(String login);



}
