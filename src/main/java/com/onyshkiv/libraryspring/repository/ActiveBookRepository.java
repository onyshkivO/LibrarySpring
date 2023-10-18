package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import com.onyshkiv.libraryspring.entity.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ActiveBookRepository extends JpaRepository<ActiveBook,Integer> {
    @Modifying
    @Query("UPDATE ActiveBook ab SET ab.subscriptionStatus = ?2 WHERE ab.id = ?1")
    int updateSubscriptionStatus(int activeBookId, SubscriptionStatus subscriptionStatus);

    @EntityGraph(attributePaths = {"book","user"})
    Page<ActiveBook> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"book","user"})
    Optional<ActiveBook> findById(Integer id);


    Page<ActiveBook> getActiveBooksByUserLogin(String login, Pageable pageable);
}
