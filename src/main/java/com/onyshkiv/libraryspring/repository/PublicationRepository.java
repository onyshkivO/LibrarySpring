package com.onyshkiv.libraryspring.repository;

import com.onyshkiv.libraryspring.DTO.PublicationDTO;
import com.onyshkiv.libraryspring.entity.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<Publication,Integer> {
}
