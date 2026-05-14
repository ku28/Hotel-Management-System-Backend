package com.hotelmanagement.hotelmanagementbackend.reservation.repository;

import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setup() {
        reservationRepository.deleteAll();
    }

    @Test
    @DisplayName("Test findByGuestEmailIgnoreCase")
    void testFindByGuestEmailIgnoreCase() {

        Reservation reservation = Reservation.builder()
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .guestPhone("9876543210")
                .checkInDate(LocalDate.of(2026, 5, 20))
                .checkOutDate(LocalDate.of(2026, 5, 25))
                .build();

        reservationRepository.save(reservation);

        Page<Reservation> result =
                reservationRepository.findByGuestEmailIgnoreCase(
                        "JOHN@EXAMPLE.COM",
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Test date range search")
    void testFindByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual() {

        Reservation reservation = Reservation.builder()
                .guestName("Alice")
                .guestEmail("alice@example.com")
                .guestPhone("9999999999")
                .checkInDate(LocalDate.of(2026, 6, 1))
                .checkOutDate(LocalDate.of(2026, 6, 5))
                .build();

        reservationRepository.save(reservation);

        Page<Reservation> result =
                reservationRepository
                        .findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
                                LocalDate.of(2026, 6, 1),
                                LocalDate.of(2026, 6, 10),
                                PageRequest.of(0, 10)
                        );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Test reservation count")
    void testCount() {

        Reservation reservation = Reservation.builder()
                .guestName("Sara")
                .guestEmail("sara@example.com")
                .guestPhone("8888888888")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(2))
                .build();

        reservationRepository.save(reservation);

        long count = reservationRepository.count();

        assertEquals(1, count);
    }
}