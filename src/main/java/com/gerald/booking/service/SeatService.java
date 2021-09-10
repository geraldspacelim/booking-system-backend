package com.gerald.booking.service;

import com.gerald.booking.model.Booking;
import com.gerald.booking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final BookingRepository bookingRepository;

    // low level service controlled by Booking Service
    // requires new is needed because it is a nested transaction and needed for optimistic locking by parent service
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
}
