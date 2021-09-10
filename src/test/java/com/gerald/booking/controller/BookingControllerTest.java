package com.gerald.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerald.booking.model.Booking;
import com.gerald.booking.service.BookingService;
import com.gerald.booking.service.SeatService;
import com.google.common.util.concurrent.RateLimiter;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.regex.Matcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// checks if the REST APIS are functioning properly
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    private BookingService bookingService;

    @MockBean
    private RateLimiter rateLimiter;

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MockMvc mockMvc;

    // checks if api retrieves all seats, returns status 200 if true
    @Test
    void findAllAvailableSeats() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/findAllSeats"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(3));
    }

    // checks if an empty seat is booked, returns status 200 if true
    @Test
    void isBooked() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/isBooked/A1"))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }

    // checks if an empty seat could be booked, returns status 200 if true
    @Test
    void bookSeat() throws Exception{
        mockMvc.perform( MockMvcRequestBuilders
                        .put("/api/v1/bookSeat")
                        .content(asJsonString(new Booking("gerald", "lim", "geraldlim6.12@gmail.com", "A1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is(200));
    }

    // helper function to convert class to JSON String
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}