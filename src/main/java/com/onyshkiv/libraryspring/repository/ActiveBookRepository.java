package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActiveBookRepository extends JpaRepository<ActiveBook,Integer> {
    @Modifying
    @Query("UPDATE ActiveBook SET subscriptionStatus = 2 WHERE activeBookId = ?1")
    int updateSubscriptionStatus(int activeBookId);
}
