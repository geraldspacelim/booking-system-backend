# booking-system-backend

The Booking System backend acts as a REST Controller to facilitate requests from the frontend and interact with the database. This application is built using Spring Boot and Maven that provides dependency management. I have also written some Junit testing for performing CRUD operations on the database and testing the REST controller Spring MockMVC. I have also implemented basic security features such such as rate limiting on the REST Controller to prevent malicious attacks. Lastly, I have also implemented Optismitic locking to handle concurrency scenarios and data inconsistency. 

This application is built using the MVC framework and its JAR file is hosted on my Digital Ocean Server.

Disclaimer: It is my first time building a Spring Boot application

## Available Scripts

### `mvc spring-boot:run`

Starts the server at [http://localhost:8001](http://localhost:8001) 

### `mvc clean package`

Creates a JAR file in the target folder for deployment

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

<img src="./screenshots/architecture backend.PNG" width="400" height="300">

The backend architecture follows that of the MVC pattern:

**Client**: The client sends HTTP requests and receives responses directly to and from the REST controllers   
**Controllers**: It is responsible for processing any incoming REST API, preparing and returning models and status entities. The @RestController annotation is used to mark the service classes  
**Service**: It contains the business logic of the application and the @Service annotation is used to mark the service classes  
**Repository**: It is responsible for storage, retrieval and seatch behavior which emulates a collection objects (e.g. model). I have written custom queries that better fit my use case. The @Repository annotation is used to mark the repository class  
**Model**: The model class represents the data object as public properties and business logic as methods. The @Entity is used to mark the Model class.  
**Response**: The Response Handler class is a custom response handler object that includes data, message and status code for better readability of responses called from the client.  

##  APIs

REST APIS of ```GET, POST, PUT``` are implemented: 

**PUT** ```/api/v1/bookSeat``` - books a seat by updating a row with first_name, last_name, email, sets is_reserved to false and version to 2   
**GET** ```/api/v1//isBooked/{id}``` - given the seat number, returns is_reserved is true if seat is taken, otherwise false  
**GET** ```/api/v1/findAllSeats``` - returns the statuses of all seats     
**POST** ```/api/v1/sendConfirmationEmail``` - if successful booking, sends a confirmation email to the user  

Error Codes: 
**200** OK + success message   
**404** NOT FOUND + error message   
**409** CONFICT + error message   
**429** TOO MANY REQUESTS + erorr message  
**500** INTERNAL SERVER ERROR + error message  

## Handling Concurrency Issues

In most RDBMS (e.g. MySQL), the transaction isolation level is at least read committed. Heence it solves dirty reads, which guarantees that the trasaction reads only committed data by other transactions. However, because of its isolation level, a conflict can happen when 2 transactions have been processed simultaneously. The later transaction will commit a bit later and overwrite the data persisted by the earlier one. 

To solve this problem, we can use either pessimistic locking or optimistic locking (some research done). In summary, Pessimistic locking takes an exlusive lock so that no one else can modify a record and optimistic lock checks the record that was updated before one commit the transaction. I have decided to use an optimistic locking mechanism becuase o different LockTimeout by supported by different RDBMS providers and is susceptible to a deadlock should one decides to hold the lock forever. 

### Booking Service
```
@Transactional(readOnly = true)
    public Booking saveSeat(Booking booking) {
        try {
            seatService.reserveSeat(booking);
            return booking;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw e;
        }
    }
```

### Seat Service

```
public void reserveSeat(Booking booking) {
        Booking retrieved_booking = bookingRepository.findBySeatNumber(booking.getSeat_number());
        // check if seat exists
        if (retrieved_booking == null) {
            throw new EntityNotFoundException("Seat number does not exist");
        }
        // for sequential updates, this will check if the seat has been reserved by someone else
        if (retrieved_booking.getIs_reserved()) {
            throw new IllegalStateException("Seat " + retrieved_booking.getSeat_number() + " has been booked by someone else!");
        }
        retrieved_booking.setFirst_name(booking.getFirst_name());
        retrieved_booking.setLast_name(booking.getLast_name());
        retrieved_booking.setEmail(booking.getEmail());
        retrieved_booking.setIs_reserved(true);

    }
```

To implement optimistic locking, we will need a version variable in the model class annotated by Version, high-level service (Booking Service) controlling the low-level service (SeatService). The Booking Service will try to update the seat row in the database by calling the Seat service where it loads the latest version of the entity from the database. If there is a version mismatch (```0 != 1```), it means that the seat has been reserved by someone and will throw an optimistic locking exception. 

## Security

I have implemented a simple rate limter using Google's Guava library to prevent repeadted calls on the API from the client side which is susceptible to malicious attack (e.g. DDOS attack). The rate limiter allows for a maximum of 10 calls per second within a 30s duration. Should a user spam API calls from the client, it will drop a TOO MANY REQUEST error.


## Test

I have implemented a few Junit test on the Database and REST controllers (MockMVC) to test if they do return the correct responses and OK status respectively. They are annotated by @Test. 

### Controller Test

<img src="./screenshots/rest controller test.PNG" width="400" height="150">

```GET /api/v1/findAllSeats```: **expect**: Status OK, **response**: Status OK   
```GET /api/v1/findAllSeats```: **expect** Status OK, **response**: Status OK   
```PUT /api/v1/bookSeat```: **expect** Status OK, **response**: Status OK  

### Service Test

<img src="./screenshots/service test.PNG" width="400" height="150">

```assertEquals(true, bookingRepository.findBySeatNumber("A1").getIs_reserved());```: **expect**: true, **response**: true  
```assertEquals(true, seatStatus)```: **expect**: true, **response**: true  
```assertThat(allSeats).size().isEqualTo(20)```: **response**: ok  
```assertEquals(1, bookingItem.getVersion())```: **expect**: 1, **response**: 1  



