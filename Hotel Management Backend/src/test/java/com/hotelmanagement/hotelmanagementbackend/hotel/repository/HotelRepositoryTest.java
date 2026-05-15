package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Test
    @DisplayName("shouldReturnHotelsByLocationIgnoreCase")
    void shouldReturnHotelsByLocationIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByLocationContainingIgnoreCase(
                        "Downtown",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);

        assertTrue(result.getTotalElements() >= 1);

        assertTrue(
                result.getContent()
                        .stream()
                        .anyMatch(h ->
                                h.getLocation()
                                        .contains("Downtown"))
        );
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameIgnoreCase")
    void shouldReturnHotelsByNameIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByNameContainingIgnoreCase(
                        "Grand Plaza",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);

        assertTrue(result.getTotalElements() >= 1);

        Hotel hotel = result.getContent().getFirst();

        assertTrue(
                hotel.getName()
                        .contains("Grand Plaza")
        );
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameOrLocation")
    void shouldReturnHotelsByNameOrLocation() {

        Page<Hotel> result =
                hotelRepository
                        .findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                                "Oceanfront",
                                "Beachfront",
                                PageRequest.of(0, 10)
                        );

        assertNotNull(result);

        assertTrue(result.getTotalElements() >= 1);

        assertTrue(
                result.getContent()
                        .stream()
                        .anyMatch(h ->
                                h.getName().contains("Oceanfront")
                                        ||
                                        h.getLocation().contains("Beachfront"))
        );
    }

    @Test
    @DisplayName("shouldReturnTrueWhenHotelExists")
    void shouldReturnTrueWhenHotelExists() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "Grand Plaza Hotel",
                        "Downtown City Center"
                );

        assertTrue(exists);
    }

    @Test
    @DisplayName("shouldReturnFalseWhenHotelDoesNotExist")
    void shouldReturnFalseWhenHotelDoesNotExist() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "HotelThatDoesNotExist123",
                        "UnknownLocation123"
                );

        assertFalse(exists);
    }
}