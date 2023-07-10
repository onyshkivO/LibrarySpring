package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PublicationService {
    private final PublicationRepository publicationRepository;

    @Autowired
    public PublicationService(PublicationRepository publicationRepository) {
        this.publicationRepository = publicationRepository;
    }

    public List<Publication> getAllPublications() {
        return publicationRepository.findAll();
    }


    @Transactional
    public void savePublication(Publication publication) {
        publicationRepository.save(publication);
    }

    @Transactional
    public void updatePublication(Integer id, Publication publication) {
        publication.setPublicationId(id);
        publicationRepository.save(publication);
    }

}
