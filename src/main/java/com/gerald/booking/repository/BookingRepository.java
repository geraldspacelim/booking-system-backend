package com.gerald.booking.repository;

import com.gerald.booking.model.Booking;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    // retrieves seat details based on a given seat number
    @Query(value = "select * from booking where seat_number = :seat_number", nativeQuery = true)
    Booking findBySeatNumber(String seat_number);

    // retrieves a list of all seats with their id, seat number and reservation status
    @Query(value="select id, seat_number, is_reserved from booking", nativeQuery = true)
    List<Object> getAllSeats();

    // retrieves a seat reservation status based on a given seat number
    @Query(value="select is_reserved from booking where seat_number = :seat_number", nativeQuery = true)
    Boolean getSeatStatus(String seat_number);

}
