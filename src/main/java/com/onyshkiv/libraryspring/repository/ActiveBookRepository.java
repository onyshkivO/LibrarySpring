package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.entity.ActiveBook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface ActiveBookRepository extends JpaRepository<ActiveBook,Integer> {
    @Modifying
    @Query("UPDATE ActiveBook SET subscriptionStatus = 2 WHERE activeBookId = ?1")
    int updateSubscriptionStatus(int activeBookId);




    List<ActiveBook> getActiveBooksByUserLogin(String login);
}
