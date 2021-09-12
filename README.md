# booking-system-backend

The Booking System backend acts as a REST Controller to facilitate requests from the frontend and interact with the database. This application is built using Spring Boot and Maven that provides dependency management. I have also written some Junit testing for performing CRUD operations on the database and testing the REST controller Spring MockMVC. I have also implemented basic security features such such as rate limiting on the REST Controller to prevent malicious attacks. Lastly, I have also implemented Optismitic locking to handle concurrency scenarios and data inconsistency. 

This application is built using the MVC framework and its JAR file is hosted on my Digital Ocean Server.

## Available Scripts

### `mvc spring-boot:run`

Starts the server at [http://localhost:8001](http://localhost:8001) 

#### schema.sql 

Upon starting the server, it will also run the schema.sql that drops any existing table and re-creates a new table booking with the following schema. We need to set ```spring.jpa.hibernate.ddl-auto=update``` and ```spring.datasource.initialization-mode=always``` in our applications.properties. 

```
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
```
#### data.sql 

In addition to creating a database on start, we will also insert 20 dummy 'seats' into the database that will be used for the front end application. 

```
insert into booking (first_name, last_name, email, seat_number) values (NULL, NULL, NULL, 'A1');
insert into booking (first_name, last_name, email, seat_number) values (NULL, NULL, NULL, 'A2');
insert into booking (first_name, last_name, email, seat_number) values (NULL, NULL, NULL, 'A3');
...
```

##  Architecture 

<img src="./screenshots/architecture backend.PNG" width="300" height="300">




