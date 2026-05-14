package com.hotelmanagement.hotelmanagementbackend.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Test
    @DisplayName("shouldReturnHotelsByLocationIgnoreCase")
    void shouldReturnHotelsByLocationIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByLocationContainingIgnoreCase(
                        "Delhi",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameIgnoreCase")
    void shouldReturnHotelsByNameIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByNameContainingIgnoreCase(
                        "Radisson",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Hotel hotel = result.getContent().getFirst();

        assertEquals("Radisson Blu", hotel.getName());
        assertEquals("Mumbai", hotel.getLocation());
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameOrLocation")
    void shouldReturnHotelsByNameOrLocation() {

        Page<Hotel> result =
                hotelRepository
                        .findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                                "Hayat",
                                "Chandigarh",
                                PageRequest.of(0, 10)
                        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Hotel hotel = result.getContent().getFirst();

        assertEquals("Hayat Regency", hotel.getName());
        assertEquals("Chandigarh", hotel.getLocation());
    }

    @Test
    @DisplayName("shouldReturnTrueWhenHotelExists")
    void shouldReturnTrueWhenHotelExists() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "Oberoi",
                        "Delhi"
                );

        assertTrue(exists);
    }

    @Test
    @DisplayName("shouldReturnFalseWhenHotelDoesNotExist")
    void shouldReturnFalseWhenHotelDoesNotExist() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "Unknown Hotel",
                        "Punjab"
                );

        assertFalse(exists);
    }
}