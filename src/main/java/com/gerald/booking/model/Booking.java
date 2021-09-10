package com.gerald.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="booking")
public class Booking {

    // booking model
    @Id
    @GeneratedValue
    private Integer id;
    private String first_name;
    private String last_name;
    private String email;
    private String seat_number;
    private Boolean is_reserved;
    // version is used for optimistic checking
    @Version
    private Long version;

    public Booking(String first_name, String last_name, String email, String seat_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.seat_number = seat_number;
    }
}
