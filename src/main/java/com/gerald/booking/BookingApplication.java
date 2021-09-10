package com.gerald.booking;

import com.gerald.booking.controller.BookingController;
import com.gerald.booking.repository.BookingRepository;
import com.gerald.booking.service.BookingService;
import com.gerald.booking.service.SeatService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class BookingApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

}
