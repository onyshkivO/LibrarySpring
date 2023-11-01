package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotFoundException;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotSavedException;
import com.onyshkiv.libraryspring.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PublicationService {
    private final PublicationRepository publicationRepository;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public DataPageDto<Publication> getAllPublications(Pageable pageable) {
        Page<Publication> publicationsPage = publicationRepository.findAll(pageable);
        return new DataPageDto<>(publicationsPage.getContent(),pageable.getPageNumber(),publicationsPage.getTotalPages());
    }

    public Publication getPublicationById(int id) {
        return publicationRepository.findById(id)
                .orElseThrow(()->new PublicationNotFoundException("there are not publication with id " + id));
    }


    @Transactional
    public Publication savePublication(Publication publication) {
        if (publication.getId() != 0)
            throw new PublicationNotSavedException("Publication with id " + publication.getId() + " already exist");
        return publicationRepository.save(publication);
    }

    @Transactional
    public Publication updatePublication(int id, Publication publication) {
        Publication publicationFromDb = getPublicationById(id);
        publicationFromDb.setName(publication.getName());
        return publicationFromDb;
    }

    @Transactional
    public void delete(int id) {
        Publication publication = getPublicationById(id);
        publication.getBooks().forEach(book->book.setPublication(null));
        publicationRepository.delete(publication);
    }
}
