drop table booking;
create table booking (
    id integer auto_increment,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255),
    seat_number varchar(255),
    is_reserved boolean default false,
    version float default 0,
    primary key (id)
);