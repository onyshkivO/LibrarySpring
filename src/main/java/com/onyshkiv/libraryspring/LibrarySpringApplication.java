package com.onyshkiv.libraryspring;

import com.onyshkiv.libraryspring.repository.BookRepository;
import com.onyshkiv.libraryspring.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LibrarySpringApplication implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Autowired
    public LibrarySpringApplication(BookRepository bookRepository, UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(LibrarySpringApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void run(String... args) throws Exception {
        //bookRepository.getAll(PageRequest.of(0, 1));
        System.out.println(userRepository.findById("user1").get().getRole());
    }
}
