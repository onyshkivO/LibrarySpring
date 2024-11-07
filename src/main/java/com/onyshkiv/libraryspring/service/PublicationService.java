package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.entity.Publication;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotFoundException;
import com.onyshkiv.libraryspring.exception.publication.PublicationNotSavedException;
import com.onyshkiv.libraryspring.repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Cacheable(key = "#id", value = "Publication")
    public Optional<Publication> getPublicationById(int id) {
        return publicationRepository.findById(id);
    }


    @Transactional
    public Publication savePublication(Publication publication) {
        if (publication.getPublicationId() != 0)
            throw new PublicationNotSavedException("Publication with id " + publication.getPublicationId() + " already exist");
        return publicationRepository.save(publication);
    }

    @Transactional
    @CacheEvict(key = "#id", value = "Publication")
    public Publication updatePublication(Integer id, Publication publication) {
        Optional<Publication> optionalPublication = publicationRepository.findById(id);
        if (optionalPublication.isEmpty())
            throw new PublicationNotFoundException("Not publication found with id " + id);
        publication.setPublicationId(id);
        return publicationRepository.save(publication);
    }

    @Transactional
    @CacheEvict(key = "#id", value = "Publication")
    public Publication deletePublicationById(int id) {
        Optional<Publication> optionalPublication = publicationRepository.findById(id);
        if (optionalPublication.isEmpty())
            throw new PublicationNotFoundException("Not publication found with id " + id);
        publicationRepository.deleteById(id);
        return optionalPublication.get();
    }

}
