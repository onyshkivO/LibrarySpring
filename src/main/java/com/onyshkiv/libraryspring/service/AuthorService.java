package com.onyshkiv.libraryspring.service;

import com.onyshkiv.libraryspring.dto.DataPageDto;
import com.onyshkiv.libraryspring.entity.Author;
import com.onyshkiv.libraryspring.exception.author.AuthorNotFoundException;
import com.onyshkiv.libraryspring.exception.author.AuthorNotSavedException;
import com.onyshkiv.libraryspring.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthorService {
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public DataPageDto<Author> getAllAuthors(Pageable pageable) {
        Page<Author> authorsPage = authorRepository.findAll(pageable);
        return new DataPageDto<>(authorsPage.getContent(), pageable.getPageNumber(), authorsPage.getTotalPages());

    }


    public Author getAuthorById(int id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("there are not author with id " + id));
    }

    @Transactional
    public Author saveAuthor(Author author) {
        if (author.getId() != 0)
            throw new AuthorNotSavedException("Author with id " + author.getId() + " already exist");
        return authorRepository.save(author);
    }

    @Transactional
    public Author updateAuthor(Author authorFromDb, Author author) {
        authorFromDb.setName(author.getName());
        return authorFromDb;
    }

    @Transactional
    public void delete(Author author) {
        author.getBooks().forEach(book->book.getAuthors().remove(author)); //test this
        authorRepository.delete(author);
    }
}
