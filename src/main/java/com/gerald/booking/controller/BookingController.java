package com.gerald.booking.controller;

import com.gerald.booking.model.Booking;
import com.gerald.booking.response.ResponseHandler;
import com.gerald.booking.service.BookingService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

// allow cross-origin sharing from frontend
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping(path = "api/v1")
@RequiredArgsConstructor
public class BookingController {

    private final RateLimiter rateLimiter;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final JavaMailSender javaMailSender;

    // put request to book a seat
    @PutMapping("/bookSeat")
    public ResponseEntity<Object> reserveSeat(@RequestBody Booking booking) {
        try {
            Booking booking_response = bookingService.saveSeat(booking);
            return ResponseHandler.generateResponse("Booking Successful!", HttpStatus.OK, booking_response);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // get request to check if a selected seat is already booked
    @GetMapping("/isBooked/{id}")
    public ResponseEntity<Object> isBooked(@PathVariable String id) {
        // rate limiter to check for spam request
        Boolean isNotSpam = rateLimiter.tryAcquire();
        if (isNotSpam) {
            try {
                Boolean seat_number_response = bookingService.getSeatStatus(id);
                return ResponseHandler.generateResponse("Get Status Successful!", HttpStatus.OK, seat_number_response);
            } catch (EntityNotFoundException e) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
            } catch (IllegalStateException e) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, please try again later");
        }
    }

    // get request to find all seats' statuses to update frontend
    @GetMapping("/findAllSeats")
    public ResponseEntity<Object> findAllAvailableSeats() {
        try {
            List<Object> availableSeats_response = bookingService.getAllSeats();
            return ResponseHandler.generateResponse("Date Retrieved!", HttpStatus.OK, availableSeats_response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    // post request to send confirmation email after successful booking
    @PostMapping("/sendConfirmationEmail")
    public ResponseEntity<Object> sendEmail(@RequestBody Booking booking) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(booking.getEmail());
        msg.setSubject("Successful Booking!");
        String confirmation_text = String.format("Dear %s %s,\n\nYou have successfully booked Seat %s.", booking.getFirst_name(), booking.getLast_name(), booking.getSeat_number());
        msg.setText(confirmation_text);
        try {
            javaMailSender.send(msg);
            return ResponseHandler.generateResponse("Confirmation email sent successfully", HttpStatus.OK, null);
        } catch (MailSendException mse) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unsuccessful mail delivery");
        }
    }

}

