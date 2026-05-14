package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
    }

    @Test
    @DisplayName("shouldReturnAvailableRooms")
    void shouldReturnAvailableRooms() {

        // Arrange
        Room room1 = Room.builder()
                .roomNumber(101)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(102)
                .isAvailable(false)
                .build();

        Room room3 = Room.builder()
                .roomNumber(103)
                .isAvailable(true)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        // Act
        Page<Room> result =
                roomRepository.findByIsAvailableTrue(
                        PageRequest.of(0, 10)
                );

        // Assert
        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getContent())
                .extracting(Room::getRoomNumber)
                .containsExactlyInAnyOrder(101, 103);
    }
}