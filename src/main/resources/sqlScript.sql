#
# SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
# SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
# SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
#
# -- -----------------------------------------------------
# -- Schema library_final_project
# -- -----------------------------------------------------
#
# -- -----------------------------------------------------
# -- Schema library_final_project
# -- -----------------------------------------------------
# CREATE SCHEMA IF NOT EXISTS `library_final_project` DEFAULT CHARACTER SET utf8 ;
# USE `library_final_project` ;
#
#
#
#
#
#
# -- -----------------------------------------------------
# -- Table `library_final_project`.`user`
# -- -----------------------------------------------------
# DROP TABLE IF EXISTS `library_final_project`.`user` ;
#
# CREATE TABLE IF NOT EXISTS `library_final_project`.`user` (
#   `login` VARCHAR(45) NOT NULL,
#   `email` VARCHAR(255) NOT NULL,
#   `password` VARCHAR(255) NOT NULL,
#   `role_id` INT NOT NULL,
#   `status_id` INT NOT NULL,
#   `first_name` VARCHAR(45) NOT NULL,
#   `last_name` VARCHAR(45) NOT NULL,
#   `phone` VARCHAR(45) NULL,
#   PRIMARY KEY (`login`),
#   UNIQUE INDEX `login_UNIQUE` (`login` ASC) VISIBLE);
#
#
# -- -----------------------------------------------------
# -- Table `library_final_project`.`publication`
# -- -----------------------------------------------------
# DROP TABLE IF EXISTS `library_final_project`.`publication` ;
#
# CREATE TABLE IF NOT EXISTS `library_final_project`.`publication` (
#   `publication_id` INT NOT NULL AUTO_INCREMENT,
#   `name` VARCHAR(45) NOT NULL,
#   PRIMARY KEY (`publication_id`),
#   CONSTRAINT UC_Name UNIQUE (name));
#
#
# -- -----------------------------------------------------
# -- Table `library_final_project`.`book`
# -- -----------------------------------------------------
# DROP TABLE IF EXISTS `library_final_project`.`book` ;
#
# CREATE TABLE IF NOT EXISTS `library_final_project`.`book` (
#   `isbn` VARCHAR(30) NOT NULL,
#   `name` VARCHAR(255) NOT NULL,
#   `date_of_publication` DATE NOT NULL,
#   `publication_id` INT NOT NULL,
#   `quantity` INT NOT NULL DEFAULT 1,
#   `details` VARCHAR(5000) NULL,
#   `is_active` boolean default true,
#   PRIMARY KEY (`isbn`),
#   UNIQUE INDEX `isbn_UNIQUE` (`isbn` ASC) VISIBLE,
#   INDEX `fk_book_publication1_idx` (`publication_id` ASC) VISIBLE,
#   CONSTRAINT `fk_book_publication1`
#     FOREIGN KEY (`publication_id`)
#     REFERENCES `library_final_project`.`publication` (`publication_id`)
#     ON DELETE CASCADE
#     ON UPDATE CASCADE);
#
#
# -- -----------------------------------------------------
# -- Table `library_final_project`.`authors`
# -- -----------------------------------------------------
# DROP TABLE IF EXISTS `library_final_project`.`authors` ;
#
# CREATE TABLE IF NOT EXISTS `library_final_project`.`authors` (
#   `authors_id` INT NOT NULL AUTO_INCREMENT,
#   `name` VARCHAR(100) NOT NULL,
#   PRIMARY KEY (`authors_id`),
#   CONSTRAINT UC_Name UNIQUE (name));
#
# -- -----------------------------------------------------
# -- Table `library_final_project`.`book_has_authors`
# -- -----------------------------------------------------
# DROP TABLE IF EXISTS `library_final_project`.`book_has_authors` ;
#
# CREATE TABLE IF NOT EXISTS `library_final_project`.`book_has_authors` (
#   `b_isbn` VARCHAR(30) NOT NULL,
#   `a_id` INT NOT NULL,
#   PRIMARY KEY (`b_isbn`, `a_id`),
#   INDEX `fk_book_has_authors_authors1_idx` (`a_id` ASC) VISIBLE,
#   INDEX `fk_book_has_authors_book1_idx` (`b_isbn` ASC) VISIBLE,
#   CONSTRAINT `fk_book_has_authors_book1`
#     FOREIGN KEY (`b_isbn`)
#     REFERENCES `library_final_project`.`book` (`isbn`)
#     ON DELETE CASCADE
#     ON UPDATE CASCADE,
#   CONSTRAINT `fk_book_has_authors_authors1`
#     FOREIGN KEY (`a_id`)
#     REFERENCES `library_final_project`.`authors` (`authors_id`)
#     ON DELETE CASCADE
#     ON UPDATE CASCADE);
#
#
#
#
#
#
# -- -----------------------------------------------------
# -- Table `library_final_project`.`active_book`
# -- -----------------------------------------------------
# DROP TABLE IF EXISTS `library_final_project`.`active_book` ;
#
# CREATE TABLE IF NOT EXISTS `library_final_project`.`active_book` (
#   `active_book_id` INT NOT NULL AUTO_INCREMENT,
#   `book_isbn` VARCHAR(30) NOT NULL,
#   `user_login` VARCHAR(45) NOT NULL,
#   `subscription_status_id` INT NOT NULL,
#   `start_date` DATE NOT NULL DEFAULT (date_format(now(),'%y-%m-%d')),
#   `end_date` DATE NOT NULL DEFAULT (PERIOD_ADD(date_format(`start_date`,'%y%m'),1)),
#   `fine` DECIMAL(9,2) NULL,
#   PRIMARY KEY (`active_book_id`),
#   UNIQUE INDEX `active_book_id_UNIQUE` (`active_book_id` ASC) VISIBLE,
#   INDEX `fk_active_book_book1_idx` (`book_isbn` ASC) VISIBLE,
#   INDEX `fk_active_book_user_idx` (`user_login` ASC) VISIBLE,
#   CONSTRAINT `fk_active_book_book1`
#     FOREIGN KEY (`book_isbn`)
#     REFERENCES `library_final_project`.`book` (`isbn`)
#     ON DELETE cascade
#     ON UPDATE cascade,
#   CONSTRAINT `fk_active_book_user`
#     FOREIGN KEY (`user_login`)
#     REFERENCES `library_final_project`.`user` (`login`)
#     ON DELETE cascade
#     ON UPDATE cascade);
#


INSERT INTO `authors` (`name`)
VALUES
('J.K. Rowling'),
('George R.R. Martin'),
('Stephen King'),
('Robert Jordan'),
('Brandon Sanderson');

INSERT INTO `publication` (`name`)
VALUES
('Bloomsbury Publishing'),
('Bantam Spectra'),
('Scribner'),
('Tor Books');

INSERT INTO `book` (`isbn`, `name`, `date_of_publication`, `publication_id`, `quantity`, `details`, `is_active`)
VALUES
('9781408855652', 'Harry Potter and the Sorcerer\'s Stone', '1997-06-26', 1, 10, 'The first book in the Harry Potter series.', 1),
('9780553801477', 'A Game of Thrones', '1996-08-01', 2, 5, 'The first book in the A Song of Ice and Fire series.', 1),
('9781501142970', 'It', '1986-09-15', 3, 3, 'A horror novel by Stephen King.', 1),
('9780312850098', 'The eye of the world', '1990-01-15', 4, 2, 'A high fantasy series by Robert Jordan and Brandon Sanderson.', 1),
('9780439064866', 'Harry Potter and the Chamber of Secrets', '1998-07-02', 1, 7, 'The second book in the Harry Potter series.', 1),
('9788831000161', 'Harry Potter and the Prisoner of Azkaban', '1999-07-08', 1, 6, 'The third book in the Harry Potter series.', 1),
('9788893819930', 'Harry Potter and the Goblet of Fire', '2000-07-08', 1, 8, 'The fourth book in the Harry Potter series.', 1);

INSERT INTO `book_has_authors` (`b_isbn`, `a_id`)
VALUES
('9781408855652', 1),
('9780553801477', 2),
('9781501142970', 3),
('9780312850098', 4),
('9780312850098', 5),
('9780439064866', 1),
('9788831000161', 1),
('9788893819930', 1);

Insert into user (login,email,password,role_id,status_id,first_name,last_name,phone) VALUES
('admin','123@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',2,0,'Admin','Admin','0988255564'),
('user1','user1@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',0,0,'Tom','Krit',null),
('blockedUser','blockedUser@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',0,1,'John','Tork','0674521156'),
('user2','user2@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',0,0,'Jean','Boros','0985644475'),
('librarian1','librarian1@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',1,0,'Vitalii','Tort','0985625675'),
('librarian2','librarian2@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',1,0,'Max','Verst','0985644145'),
('librarian3','librarian3@gmail.com','$2a$12$bqjD7W/Ez8sPcRVJtUMlgOErxUDHYBO/ztj5RLJ7cvFPesTNfZwq2',1,0,'Israel','Ezze','0685254145');

Insert into active_book (book_isbn,user_login,subscription_status_id,start_date,end_date,fine) values
('9781408855652','user1',0,'2023-06-05','2023-06-30',100),
('9780553801477','user1',2,'2023-05-05','2023-06-01',100),
('9788893819930','user1',0,'2023-06-01','2023-07-15',100),
('9788831000161','user1',3,'2023-06-01','2023-06-01',null),
('9780312850098','user2',1,'2023-05-01','2023-06-01',100),
('9780553801477','user2',0,'2023-06-01','2023-06-20',100),
('9788831000161','user2',2,'2023-05-01','2023-05-30',100),
('9780439064866','user2',3,'2023-06-01','2023-06-01',null),
('9781501142970','user2',0,'2023-06-03','2023-06-25',100),
('9781501142970','blockedUser',1,'2023-03-01','2023-05-15',100);

SET FOREIGN_KEY_CHECKS=1;








