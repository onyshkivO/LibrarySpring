
create table active_book
(
    end_date            date,
    fine                float(53),
    id                  integer                                      not null auto_increment,
    start_date          date                                         not null,
    book_isbn           varchar(255)                                 not null,
    subscription_status enum ('ACTIVE','FINED','RETURNED','WAITING') not null,
    user_login          varchar(255)                                 not null,
    primary key (id)
) engine = InnoDB;
create table author
(
    id   integer not null auto_increment,
    name varchar(255),
    primary key (id)
) engine = InnoDB;
create table book
(
    date_of_publication date,
    publication_id      integer      not null,
    quantity            integer      not null,
    details             varchar(255),
    isbn                varchar(255) not null,
    name                varchar(255) not null,
    primary key (isbn)
) engine = InnoDB;
create table book_has_authors
(
    a_id   integer      not null,
    b_isbn varchar(255) not null,
    primary key (a_id, b_isbn)
) engine = InnoDB;
create table publication
(
    id   integer      not null auto_increment,
    name varchar(255) not null,
    primary key (id)
) engine = InnoDB;
create table user
(
    email      varchar(255)                                               not null,
    first_name varchar(255)                                               not null,
    last_name  varchar(255)                                               not null,
    login      varchar(255)                                               not null,
    password   varchar(255)                                               not null,
    phone      varchar(255),
    role       enum ('ROLE_ADMINISTRATOR','ROLE_LIBRARIAN','ROLE_READER') not null,
    status     enum ('ACTIVE','BLOCKED')                                  not null,
    primary key (login)
) engine = InnoDB;
alter table active_book
    add constraint FKkkv5ouf9h08mqnwjvyrggnsyn foreign key (book_isbn) references book (isbn);
alter table active_book
    add constraint FKplsg0arypryjdlfh3jcn2yj9l foreign key (user_login) references user (login);
alter table book
    add constraint FKg8qaca1jrg0ol4388sv04bpcv foreign key (publication_id) references publication (id);
alter table book_has_authors
    add constraint FKt0a0koffpd1wmpborfskgtoi8 foreign key (a_id) references author (id);
alter table book_has_authors
    add constraint FKkp5y45t0uofga67plhcfew9fv foreign key (b_isbn) references book (isbn);