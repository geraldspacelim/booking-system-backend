package com.gerald.booking.service;

import com.gerald.booking.model.Booking;
import com.gerald.booking.repository.BookingRepository;
import com.google.common.util.concurrent.RateLimiter;
import lombok.NoArgsConstructor;
import org.apache.el.parser.BooleanNode;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private  BookingRepository bookingRepository;

    @Autowired
    private SeatService seatService;

    // treated as as atomic action, read only prevents dirty checking
    // high-level service that controls the seat service
    @Transactional(readOnly = true)
    public Booking saveSeat(Booking booking) {
        try {
            seatService.reserveSeat(booking);
            return booking;
        } catch (ObjectOptimisticLockingFailureException e) {
            throw e;
        }
    }

    // check for seat status
    public Boolean getSeatStatus(String seat_number) {
        Boolean seat_status = bookingRepository.getSeatStatus(seat_number);
        if (seat_status == null) {
            throw new EntityNotFoundException("Seat number does not exist");
        } else {
            return bookingRepository.getSeatStatus(seat_number);
        }

    }

    // retrieves all seats' details
    public List<Object> getAllSeats() {
        try {
            return bookingRepository.getAllSeats();
        } catch (Exception e) {
            throw e;
        }

    }
}
