package com.gerald.booking.service;

import com.gerald.booking.model.Booking;
import com.gerald.booking.repository.BookingRepository;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.print.Book;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

// checks if the services are functioning properly
@SpringBootTest
class BookingServiceTest {

    @Autowired
    BookingService bookingService;

    @Autowired
    SeatService seatService;

    @Autowired
    BookingRepository bookingRepository;

    // save a seat and checks if is_reserved is true
    @Test
    void saveSeat() {
        Booking booking = bookingRepository.findBySeatNumber("A1");
        booking.setFirst_name("Gerald");
        booking.setLast_name("lim");
        booking.setEmail("geraldlim6.12@gmail.com");
        booking.setIs_reserved(true);
        bookingRepository.save(booking);
        assertEquals(true, bookingRepository.findBySeatNumber("A1").getIs_reserved());
    }

    // checks if empty seat is_reserved is true (after previous test)
    @Test
    void getSeatStatus() {
        Boolean seatStatus = bookingRepository.getSeatStatus("A1");
        assertEquals(true, seatStatus);
    }

    // checks if returns the number of seats instantiated
    @Test
    void getAllSeats() {
        List<Object> allSeats = bookingRepository.getAllSeats();
        assertThat(allSeats).size().isEqualTo(20);
    }

    private final List<Booking> bookings = Arrays.asList(
            new Booking("John", "Tan", "John@gmail.com", "A4"),
            new Booking("Peter", "Lim", "Peter@gmail.com", "A4"));

    // 2 concurrent threads that tries to book the same seat
    // checks if only 1 user managed to book a seat successfully
    @Test
    void shouldNotOverwrite_usingOptimisticLockingHandling() throws InterruptedException {
        final ExecutorService executor = Executors.newFixedThreadPool(bookings.size());

        for (final Booking bookingItem : bookings) {
            executor.execute(() -> bookingService.saveSeat(bookingItem));
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        final Booking bookingItem = bookingRepository.findBySeatNumber("A4");
        assertEquals(1, bookingItem.getVersion());

    }
}