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

import java.util.List;
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
        return new DataPageDto<>(authorsPage.getContent(),pageable.getPageNumber(),authorsPage.getTotalPages());

    }


    public Optional<Author> getAuthorById(int id) {
        return authorRepository.findById(id);

    }

    @Transactional
    public Author saveAuthor(Author author) {
        if (author.getId() != 0)
            throw new AuthorNotSavedException("Author with id " + author.getId() + " already exist");
        return authorRepository.save(author);
    }

    @Transactional
    public Author updateAuthor(Author authorFromDb, Author author) {
//        Optional<Author> optionalAuthor = authorRepository.findById(id);
//        if (optionalAuthor.isEmpty())
//            throw new AuthorNotFoundException("Not author found with id " + id);
//        author.setId(id);
        authorFromDb.setName(author.getName());
        return authorRepository.save(author);
    }

    @Transactional
    public Author deleteAuthorById(int id) {
        Optional<Author> optionalAuthor = authorRepository.findById(id);
        if (optionalAuthor.isEmpty())
            throw new AuthorNotFoundException("Not author found with id " + id);
        authorRepository.deleteById(id);
        return optionalAuthor.get();
    }
    @Transactional
    public void delete(Author author) {
        //хз може треба цю перевірку
//        if (author==null)
//            throw new AuthorNotFoundException("Not author found with id ");
        authorRepository.delete(author);
    }
}
