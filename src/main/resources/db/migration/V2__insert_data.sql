INSERT INTO author (name)
VALUES ('J.K. Rowling'),
       ('George R.R. Martin'),
       ('Stephen King'),
       ('Robert Jordan'),
       ('Brandon Sanderson');

INSERT INTO `publication` (`name`)
VALUES ('Bloomsbury Publishing'),
       ('Bantam Spectra'),
       ('Scribner'),
       ('Tor Books');

INSERT INTO `book` (`isbn`, `name`, `date_of_publication`, `publication_id`, `quantity`, `details`)
VALUES ('9781408855652',
        'Harry Potter and the Sorcerer\'s Stone', '1997-06-26', 1, 10, 'The first book in the Harry Potter series.'),
       ('9780553801477', 'A Game of Thrones', '1996-08-01', 2, 5,
        'The first book in the A Song of Ice and Fire series.'),
       ('9781501142970', 'It', '1986-09-15', 3, 3, 'A horror novel by Stephen King.'),
       ('9780312850098', 'The eye of the world', '1990-01-15', 4, 2,
        'A high fantasy series by Robert Jordan and Brandon Sanderson.'),
       ('9780439064866', 'Harry Potter and the Chamber of Secrets', '1998-07-02', 1, 7,
        'The second book in the Harry Potter series.'),
       ('9788831000161', 'Harry Potter and the Prisoner of Azkaban', '1999-07-08', 1, 6,
        'The third book in the Harry Potter series.'),
       ('9788893819930', 'Harry Potter and the Goblet of Fire', '2000-07-08', 1, 8,
        'The fourth book in the Harry Potter series.');

INSERT INTO `book_has_authors` (`b_isbn`, `a_id`)
VALUES ('9781408855652', 1),
       ('9780553801477', 2),
       ('9781501142970', 3),
       ('9780312850098', 4),
       ('9780312850098', 5),
       ('9780439064866', 1),
       ('9788831000161', 1),
       ('9788893819930', 1);

Insert into user (login, email, password, role, status, first_name, last_name, phone)
VALUES ('admin', '123@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2', 'ROLE_ADMINISTRATOR',
        'ACTIVE', 'Admin', 'Admin', '0988255564'),
       ('user1', 'user1@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2', 'ROLE_READER',
        'ACTIVE', 'Tom', 'Krit', null),
       ('blockedUser', 'blockedUser@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',
        'ROLE_READER', 1, 'John', 'Tork', '0674521156'),
       ('user2', 'user2@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2', 'ROLE_READER',
        'ACTIVE', 'Jean', 'Boros', '0985644475'),
       ('librarian1', 'librarian1@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',
        'ROLE_LIBRARIAN', 'ACTIVE', 'Vitalii', 'Tort', '0985625675'),
       ('librarian2', 'librarian2@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',
        'ROLE_LIBRARIAN', 'ACTIVE', 'Max', 'Verst', '0985644145'),
       ('librarian3', 'librarian3@gmail.com', '$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',
        'ROLE_LIBRARIAN', 'ACTIVE', 'Israel', 'Ezze', '0685254145');

Insert into active_book (book_isbn, user_login, subscription_status, start_date, end_date, fine)
values ('9781408855652', 'user1', 'ACTIVE', '2023-06-05', '2023-06-30', 100),
       ('9780553801477', 'user1', 'FINED', '2023-05-05', '2023-06-01', 100),
       ('9788893819930', 'user1', 'ACTIVE', '2023-06-01', '2023-07-15', 100),
       ('9788831000161', 'user1', 'WAITING', '2023-06-01', '2023-06-01', null),
       ('9780312850098', 'user2', 'RETURNED', '2023-05-01', '2023-06-01', 100),
       ('9780553801477', 'user2', 'ACTIVE', '2023-06-01', '2023-06-20', 100),
       ('9788831000161', 'user2', 'FINED', '2023-05-01', '2023-05-30', 100),
       ('9780439064866', 'user2', 'WAITING', '2023-06-01', '2023-06-01', null),
       ('9781501142970', 'user2', 'ACTIVE', '2023-06-03', '2023-06-25', 100),
       ('9781501142970', 'blockedUser', 'RETURNED', '2023-03-01', '2023-05-15', 100);